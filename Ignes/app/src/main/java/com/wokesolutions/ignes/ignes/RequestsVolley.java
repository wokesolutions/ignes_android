package com.wokesolutions.ignes.ignes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.IntentCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;

public class RequestsVolley {

    private static final int SERVER_ERROR = 500;
    private static final int NO_CONTENT_ERROR = 204;
    private static final int NOT_FOUND_ERROR = 404;
    private static final int BAD_REQUEST_ERROR = 400;
    private static final String URL = "https://mimetic-encoder-209111.appspot.com/api";

    private static final int L_EVERYWHERE = 1;
    private static final int L_ONCE = 0;

    private static StringRequest stringRequest;
    private static JsonObjectRequest jsonRequest;
    private static JsonArrayRequest arrayRequest;
    private static String url, mIsFinish;

    public static void leaderboardRequest(final Context context,
                                          final LeaderboardActivity activity) {

        final SharedPreferences sharedPref = context.getSharedPreferences("Shared", Context.MODE_PRIVATE);
        final String mToken = sharedPref.getString("token", null);

        System.out.println("LEADERBOARD REQUEST");

        RequestQueue queue = Volley.newRequestQueue(context);

        url = URL + "/profile/usertop";

        arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // response
                        System.out.println("OK LEADERBOARD: " + response);
                        activity.setLeaderboard(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error.networkResponse != null) {
                            System.out.println("ERRO DO REPORTLEADERBOARD:: " + error.networkResponse.toString());
                        } else
                            System.out.println("REPORT LEADERBOARD: volley -> ERRO Response veio null ");

                        Toast.makeText(context, "Something went wrong on loading leaderboard!", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(mToken, context);
            }
        };

        setRetry(arrayRequest);
        queue.add(arrayRequest);
    }

    public static void commentDeleteRequest(String commentId, final MarkerActivity activity,
                                            final Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);
        final String mComment = commentId;
        final SharedPreferences sharedPref = context.getSharedPreferences("Shared", Context.MODE_PRIVATE);
        final String mToken = sharedPref.getString("token", null);

        String url = URL + "/report/comment/delete/" + mComment;

        stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("OK APAGAR COMENTARIO: " + response);

                        Toast.makeText(context, "Comentário apagado!", Toast.LENGTH_LONG).show();
                        activity.recreate();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("ERRO DO APAGAR COMENTARIO: " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(mToken, context);
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                System.out.println("PARSE RESPONSE STATUS CODE --->>" + response);

                if (response.statusCode == 200) {
                    return Response.success("Apagado com sucesso", HttpHeaderParser.parseCacheHeaders(response));
                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else if (response.statusCode == NO_CONTENT_ERROR) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }
        };
        setRetry(stringRequest);

        queue.add(stringRequest);
    }

    public static void reportDeleteRequest(final String reportId,
                                           final FeedActivity feedActivity,
                                           final ProfileActivity profileActivity, final Context context,
                                           final android.support.v7.app.AlertDialog alertDialog) {

        RequestQueue queue = Volley.newRequestQueue(context);
        final String mReport = reportId;
        final SharedPreferences sharedPref = context.getSharedPreferences("Shared", Context.MODE_PRIVATE);
        final String mToken = sharedPref.getString("token", null);
        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("info", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = URL + "/report/delete/" + mReport;

        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("OK APAGARO OCORRENCIA: " + response);
                        Toast.makeText(context, "Ocorrência apagada!", Toast.LENGTH_LONG).show();

                        MarkerAdapter.mMap.remove(reportId);
                        MapActivity.userMarkerMap.remove(reportId);
                        if (MapActivity.mReportMap.get(reportId) != null)
                            MapActivity.mReportMap.remove(reportId);

                        if (feedActivity != null)
                            feedActivity.recreate();
                        else if (profileActivity != null)
                            profileActivity.recreate();

                        alertDialog.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("ERRO DO APAGAR OCORRENCIA: " + error.toString() + " " + error.networkResponse.statusCode);

                        if (error.networkResponse.statusCode == 403)
                            Toast.makeText(context, "Não pode apagar uma ocorrência a ser tratada.", Toast.LENGTH_LONG).show();

                        alertDialog.dismiss();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(mToken, context);
            }

            @Override
            public byte[] getBody() {
                return jsonObject.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                System.out.println("PARSE RESPONSE STATUS CODE --->>" + response);

                if (response.statusCode == 200) {
                    return Response.success("Apagado com sucesso", HttpHeaderParser.parseCacheHeaders(response));
                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else if (response.statusCode == NO_CONTENT_ERROR) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }
        };
        setRetry(stringRequest);

        queue.add(stringRequest);
    }


    public static void reportAcceptApplicationRequest(String report, String nif,
                                                      final ApplicationActivity activity, final Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);
        final String mNif = nif;
        final String mReport = report;
        final SharedPreferences sharedPref = context.getSharedPreferences("Shared", Context.MODE_PRIVATE);
        final String mToken = sharedPref.getString("token", null);
        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("report", mReport);
            jsonObject.put("nif", mNif);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = URL + "/report/acceptapplication";


        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("OK ACEITAR APPLICATION: " + response);

                        Toast.makeText(context, "Application Accepted!", Toast.LENGTH_LONG).show();
                        activity.recreate();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("ERRO DO ACEITAR APPLICATION: " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(mToken, context);
            }

            @Override
            public byte[] getBody() {
                return jsonObject.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                System.out.println("PARSE RESPONSE STATUS CODE --->>" + response);

                if (response.statusCode == 200) {
                    return Response.success("Application Accepted", HttpHeaderParser.parseCacheHeaders(response));
                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else if (response.statusCode == NO_CONTENT_ERROR) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }
        };
        setRetry(stringRequest);

        queue.add(stringRequest);
    }


    public static void reportApplicationsRequest(String reportId, final Context context,
                                                 final ProfileActivity activity) {

        final String mReportId = reportId;

        final SharedPreferences sharedPref = context.getSharedPreferences("Shared", Context.MODE_PRIVATE);
        final String mToken = sharedPref.getString("token", null);

        RequestQueue queue = Volley.newRequestQueue(context);

        url = URL + "/report/getapplications/" + mReportId;


        arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // response
                        System.out.println("OK REPORT APPLICATIONS ORGS : " + response);

                        activity.setListApplications(response, mReportId);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error.networkResponse != null)
                            System.out.println("ERRO DO REPORT APPLICATIONS ORGS : " + error.networkResponse.toString());
                        else
                            System.out.println("REPORT APPLICATIONS ORGS volley -> ERRO Response veio null ");

                        Toast.makeText(context, "Something went wrong on loading applications!", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(mToken, context);
            }

            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {

                System.out.println("PARSE RESPONSE STATUS CODE --->>" + response.statusCode);

                if (response.statusCode == 200) {
                    try {
                        String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        JSONArray jsonArray = new JSONArray(json);

                        return Response.success(jsonArray, HttpHeaderParser.parseCacheHeaders(response));
                    } catch (Exception e) {
                        e.printStackTrace();

                        return Response.error(new VolleyError(String.valueOf(response.statusCode)));
                    }
                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else if (response.statusCode == NO_CONTENT_ERROR) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }
        };
        setRetry(arrayRequest);

        queue.add(arrayRequest);
    }

    public static void userFollowLocalityRequest(String locality, String token,
                                                 final Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);
        final String mLocality = locality;
        final String mToken = token;

        String url = URL + "/profile/addfollow/" + mLocality;

        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("OK FOLLOW LOCALITY: " + response);
                        Toast.makeText(context, "Locality added!", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("ERRO DO FOLLOW LOCALITY: " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(mToken, context);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                System.out.println("PARSE RESPONSE STATUS CODE --->>" + response.statusCode);

                if (response.statusCode == 200) {
                    return Response.success("Votes sent", HttpHeaderParser.parseCacheHeaders(response));
                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else if (response.statusCode == NO_CONTENT_ERROR) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }
        };
        setRetry(stringRequest);

        queue.add(stringRequest);
    }

    public static void userLocalitiesRequest(final Context context,
                                             final FeedActivity activity) {

        final SharedPreferences sharedPref = context.getSharedPreferences("Shared", Context.MODE_PRIVATE);
        final String mToken = sharedPref.getString("token", null);

        RequestQueue queue = Volley.newRequestQueue(context);

        url = URL + "/profile/follows";

        arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // response
                        System.out.println("OK USER LOCALITIES : " + response);
                        activity.setListLocalities(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error.networkResponse != null)
                            System.out.println("ERRO DO USER LOCALITIES: " + error.networkResponse.toString());
                        else
                            System.out.println("USER LOCALITIES volley -> ERRO Response veio null ");

                        Toast.makeText(context, "Something went wrong on getting your localities!", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(mToken, context);
            }

            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {

                System.out.println("PARSE RESPONSE STATUS CODE --->>" + response.statusCode);

                if (response.statusCode == 200) {

                    try {
                        String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        JSONArray jsonArray = new JSONArray(json);

                        return Response.success(jsonArray, HttpHeaderParser.parseCacheHeaders(response));

                    } catch (Exception e) {
                        e.printStackTrace();

                        return Response.error(new VolleyError(String.valueOf(response.statusCode)));
                    }

                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else if (response.statusCode == NO_CONTENT_ERROR) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }
        };
        setRetry(arrayRequest);

        queue.add(arrayRequest);
    }

    public static void changeSendEmailRequest(String token, final Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);

        final String mToken = token;

        String url = URL + "/profile/changesendemail";

        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("OK CHANGE EMAIL NOTIFI: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("ERRO DO CHANGE EMAIL NOTIFI: " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(mToken, context);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                System.out.println("PARSE RESPONSE STATUS CODE --->>" + response.statusCode);

                if (response.statusCode == 200) {
                    return Response.success("Email notifications changed", HttpHeaderParser.parseCacheHeaders(response));
                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else if (response.statusCode == NO_CONTENT_ERROR) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }
        };
        setRetry(stringRequest);

        queue.add(stringRequest);
    }

    public static void reportCommentsRequest(String reportId, String cursor,
                                             final Context context,
                                             final MarkerActivity activity) {

        final String mReportId = reportId;
        final String mCursor = cursor;

        final SharedPreferences sharedPref = context.getSharedPreferences("Shared", Context.MODE_PRIVATE);
        final String mToken = sharedPref.getString("token", null);

        RequestQueue queue = Volley.newRequestQueue(context);

        url = URL + "/report/comment/get/" + mReportId + "?cursor=" + mCursor;

        arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // response
                        System.out.println("OK REPORT COMMENTS : " + response);

                        if (mIsFinish.equals("FINISHED")) {
                            System.out.println("ACABARAM OS COMMENTS");
                            activity.setListComments(response);
                        } else {
                            System.out.println("Continuar a pedir...");
                            activity.setListComments(response);
                            reportCommentsRequest(mReportId, mIsFinish, context, activity);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error.networkResponse != null) {
                            System.out.println("ERRO DO REPORT COMMENTS: " + error.networkResponse.toString());
                        } else
                            System.out.println("REPORT COMMENTS volley -> ERRO Response veio null ");

                        Toast.makeText(context, "Something went wrong on loading comments!", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(mToken, context);
            }

            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {

                System.out.println("PARSE RESPONSE STATUS CODE --->>" + response.statusCode);

                if (response.statusCode == 200) {
                    try {
                        if (response.headers.get("Cursor") != null)
                            mIsFinish = response.headers.get("Cursor");
                        else
                            mIsFinish = "FINISHED";

                        String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        JSONArray jsonArray = new JSONArray(json);

                        return Response.success(jsonArray, HttpHeaderParser.parseCacheHeaders(response));

                    } catch (Exception e) {
                        e.printStackTrace();

                        return Response.error(new VolleyError(String.valueOf(response.statusCode)));
                    }

                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else if (response.statusCode == NO_CONTENT_ERROR) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }
        };
        setRetry(arrayRequest);

        queue.add(arrayRequest);
    }

    public static void thumbnailRequest(String reportId, MarkerClass marker, final int position,
                                        final Context mContext, final MarkerAdapter markerAdapter,
                                        TaskClass task, final TaskAdapter taskAdapter, final MarkerActivity activity) {

        final String report = reportId;

        final MarkerClass mMarker = marker;

        final TaskClass mTask = task;

        ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            // If no connectivity, cancel task and update Callback with null data.
            Toast.makeText(mContext, "No Internet Connection!", Toast.LENGTH_LONG).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(mContext);

        String url = URL + "/report/thumbnail/" + report;

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        System.out.println("OK: " + response);
                        try {

                            String base64 = response.getString("thumbnail");
                            byte[] data = Base64.decode(base64, Base64.DEFAULT);

                            if (mMarker != null) {
                                mMarker.makeImg(data);
                                if (position != -1)
                                    markerAdapter.notifyItemChanged(position);

                            } else if (mTask != null) {
                                mTask.makeImg(data);
                                if (position != -1)
                                    taskAdapter.notifyItemChanged(position);
                            }
                            if (activity != null)
                                activity.marker_image.setImageBitmap(mMarker.getmImg_bitmap());


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        if (response != null) {

                            System.out.println("THUMBNAIL volley -> ERRO " + response);
                        } else
                            System.out.println("THUMBNAIL volley -> ERRO Response veio null ");


                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                //Map<String, String> params = new HashMap<String, String>();


                return setHeaders("", mContext);
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

                if (response.statusCode == 200) {

                    try {
                        String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        JSONObject jsonobject = new JSONObject(json);

                        return Response.success(jsonobject, HttpHeaderParser.parseCacheHeaders(response));

                    } catch (Exception e) {
                        e.printStackTrace();
                        return Response.error(new VolleyError(String.valueOf(response.statusCode)));
                    }
                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                1,  // maxNumRetries = 0 means no retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(postRequest);

    }

    public static void mapRequest(double lat, double lng, double radius, String token, String
            cursor,
                                  final Context context, final MapActivity activity) {

        final double mLat = lat;
        final double mLng = lng;
        final double mRadius = radius;
        final String mToken = token;
        final String mCursor = cursor;

        try {
            activity.addresses = activity.mCoder.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String mLocality = activity.addresses.get(0).getLocality();

        String url = "";

        if (activity.mRole.equals("USER")) {
            url = URL + "/report/getwithinradius?"
                    + "lat=" + activity.mCurrentLocation.getLatitude() + "&lng=" + activity.mCurrentLocation.getLongitude()
                    + "&radius=" + mRadius + "&cursor=" + mCursor;
        } else if (activity.mRole.equals("WORKER")) {
            url = URL + "/worker/tasks/" + activity.mUsername + "?cursor=" + mCursor;
        }

        arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // response
                        System.out.println("OK MAPA: " + response);

                        activity.isReady = true;

                        if (activity.mRole.equals("USER"))
                            activity.setMarkers(response, mLat, mLng, mLocality, false);
                        else if (activity.mRole.equals("WORKER"))
                            activity.setWorkerMarkers(response, mLat, mLng, mLocality);

                        if (mIsFinish.equals("FINISHED")) {
                            System.out.println("ACABARAM OS REPORTS");
                            if (activity.mRole.equals("USER"))
                                activity.votesRequest(activity.mUsername, "");
                        } else {
                            System.out.println("Continuar a pedir...");
                            activity.mapRequest(mLat, mLng, mRadius, mToken, mIsFinish);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("ERRO DO MAPA: " + error.toString());

                        if (error.toString().equals("com.android.volley.VolleyError: 204")) {
                            Toast.makeText(context, "No reports to show in this area!", Toast.LENGTH_LONG).show();
                            activity.isReady = true;
                        } else
                            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                if (activity.mRole.equals("WORKER"))
                    return setHeaders(mToken, context);
                else
                    return setHeaders(mToken, context);
            }

            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {

                System.out.println("PARSE RESPONSE STATUS CODE --->>" + response.statusCode);

                if (response.statusCode == 200) {

                    try {
                        System.out.println("PRINT DO CURSOR: " + response.headers.get("Cursor"));
                        if (response.headers.get("Cursor") != null)
                            mIsFinish = response.headers.get("Cursor");
                        else
                            mIsFinish = "FINISHED";

                        String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        JSONArray jsonArray = new JSONArray(json);

                        return Response.success(jsonArray, HttpHeaderParser.parseCacheHeaders(response));

                    } catch (Exception e) {
                        e.printStackTrace();

                        return Response.error(new VolleyError(String.valueOf(response.statusCode)));
                    }

                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else if (response.statusCode == NO_CONTENT_ERROR) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }
        };
        setRetry(arrayRequest);

        activity.queue.add(arrayRequest);
    }

    public static void locationReportsRequest(double lat, double lng, String location, String
            token,
                                              String cursor, final Context context, final MapActivity activity) {

        final double mLat = lat;
        final double mLng = lng;
        final String mLocation = location;
        final String mToken = token;
        final String mCursor = cursor;

        System.out.println("PEDIR REPORTS DE --->" + mLocation);

        String url = URL + "/report/getinlocation?location=" + mLocation + "&cursor=" + mCursor;

        arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // response
                        System.out.println("OK LOCATION: " + response);
                        activity.clearMap();
                        activity.setMarkers(response, mLat, mLng, mLocation, true);

                        if (mIsFinish.equals("FINISHED")) {
                            System.out.println("ACABARAM OS REPORTS");
                            activity.votesRequest(MapActivity.mUsername, "");
                        } else {
                            System.out.println("Continuar a pedir...");
                            activity.locationReportsRequest(mLat, mLng, mLocation, mToken, mIsFinish);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("ERRO DO LOCATION: " + error.toString());

                        if (error.toString().equals("com.android.volley.VolleyError: 204")) {
                            Toast.makeText(context, "No reports to show in this area!", Toast.LENGTH_LONG).show();

                        } else
                            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                if (MapActivity.mRole.equals("WORKER"))
                    return setHeaders(mToken, context);
                else
                    return setHeaders(mToken, context);
            }

            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {

                System.out.println("PARSE RESPONSE STATUS CODE --->>" + response.statusCode);

                if (response.statusCode == 200) {

                    try {
                        if (response.headers.get("Cursor") != null)
                            mIsFinish = response.headers.get("Cursor");
                        else
                            mIsFinish = "FINISHED";

                        String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        JSONArray jsonArray = new JSONArray(json);

                        return Response.success(jsonArray, HttpHeaderParser.parseCacheHeaders(response));

                    } catch (Exception e) {
                        e.printStackTrace();

                        return Response.error(new VolleyError(String.valueOf(response.statusCode)));
                    }

                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else if (response.statusCode == NO_CONTENT_ERROR) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }
        };
        setRetry(arrayRequest);

        MapActivity.queue.add(arrayRequest);
    }

    public static void userReportsRequest(final String username, String token, String cursor,
                                          final Context context, final ProfileActivity activity) {

        final String mUsername = username;
        final String mToken = token;
        final String mCursor = cursor;


        String url = URL + "/profile/reports/" + mUsername + "?cursor=" + mCursor;


        arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // response
                        System.out.println("OK REPORTS: " + response);

                        if (mIsFinish.equals("FINISHED")) {
                            System.out.println("ACABARAM OS REPORTS");

                            activity.setUserMap(response);
                            activity.markerAdapter = new MarkerAdapter(context, MapActivity.userMarkerMap,
                                    null, activity, username);
                            activity.recyclerView.setAdapter(activity.markerAdapter);
                            activity.recyclerView.setNestedScrollingEnabled(false);

                        } else {
                            System.out.println("Continuar a pedir...");
                            activity.userReportsRequest(mIsFinish);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("ERRO DO USER REPORTS: " + error.toString());

                        if (error.toString().equals("com.android.volley.VolleyError: 204"))
                            Toast.makeText(context, "This user has no reports!", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(mToken, context);
            }

            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {

                System.out.println("PARSE RESPONSE STATUS CODE --->>" + response.statusCode);

                if (response.statusCode == 200) {

                    try {
                        if (response.headers.get("Cursor") != null)
                            mIsFinish = response.headers.get("Cursor");
                        else
                            mIsFinish = "FINISHED";

                        String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                        JSONArray jsonArray = new JSONArray(json);

                        return Response.success(jsonArray, HttpHeaderParser.parseCacheHeaders(response));

                    } catch (Exception e) {
                        e.printStackTrace();

                        return Response.error(new VolleyError(String.valueOf(response.statusCode)));
                    }

                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else if (response.statusCode == NO_CONTENT_ERROR) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }
        };
        setRetry(arrayRequest);

        activity.queue.add(arrayRequest);
    }

    public static void taskNotesRequest(String taskID, String token, String cursor,
                                        final Context context, final NoteActivity activity) {

        final String mTask = taskID;
        final String mToken = token;
        final String mCursor = cursor;


        String url = URL + "/task/notes/" + mTask;


        arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // response
                        System.out.println("OK NOTES: " + response);

                        if (mIsFinish.equals("FINISHED")) {
                            System.out.println("ACABARAM AS NOTAS");

                            activity.setNotesList(response);
                        } else {
                            System.out.println("Continuar a pedir...");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("ERRO DO TASK NOTES: " + error.toString());
                        if (error.toString().equals("com.android.volley.VolleyError: 204"))
                            Toast.makeText(context, "Zero notes found for this task", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(mToken, context);
            }

            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {

                System.out.println("PARSE RESPONSE STATUS CODE --->>" + response.statusCode);

                if (response.statusCode == 200) {

                    try {
                        if (response.headers.get("Cursor") != null)
                            mIsFinish = response.headers.get("Cursor");
                        else
                            mIsFinish = "FINISHED";

                        String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                        JSONArray jsonArray = new JSONArray(json);

                        return Response.success(jsonArray, HttpHeaderParser.parseCacheHeaders(response));

                    } catch (Exception e) {
                        e.printStackTrace();

                        return Response.error(new VolleyError(String.valueOf(response.statusCode)));
                    }

                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else if (response.statusCode == NO_CONTENT_ERROR) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }
        };
        setRetry(arrayRequest);

        activity.queue.add(arrayRequest);
    }

    public static void sendAllVotesRequest(JSONObject votes, String token, final Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);

        final JSONObject mVotes = votes;
        final String mToken = token;


        String url = URL + "/report/vote/multiple";

        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("OK SEND VOTES: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("ERRO DO USER REPORTS: " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(mToken, context);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                System.out.println("PARSE RESPONSE STATUS CODE --->>" + response);

                if (response.statusCode == 200) {

                    return Response.success("Votes sent", HttpHeaderParser.parseCacheHeaders(response));

                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else if (response.statusCode == NO_CONTENT_ERROR) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }

            @Override
            public byte[] getBody() {
                System.out.println("WOOOOOW " + mVotes.toString());
                return mVotes.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        setRetry(stringRequest);

        queue.add(stringRequest);
    }

    public static void reportRequest(String description, String title, String district, String
            address,
                                     String locality, String category, int gravity, LatLng latLng,
                                     JSONArray jsonArray, int orientation, final Context context,
                                     final ReportFormActivity activity) {

        final String base64Img;
        // final String base64Thumbnail = Base64.encodeToString(mThumbnail, Base64.DEFAULT);
        final String mDescription = description;
        final int mGravity = gravity;
        final String mTitle = title;
        final String mDistrict = district;
        final String mAddress = address;
        final String mLocality = locality;
        final String mCategory = category;
        final int mOrientation = orientation;

        final SharedPreferences sharedPref = context.getSharedPreferences("Shared", Context.MODE_PRIVATE);
        final String token = sharedPref.getString("token", null);

        final JSONObject report = new JSONObject();

        try {
            InputStream imageStream = activity.getContentResolver().openInputStream(activity.mImageURI);

            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            ByteArrayOutputStream imgStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, imgStream);
            activity.imgByteArray = imgStream.toByteArray();
            base64Img = Base64.encodeToString(activity.imgByteArray, Base64.DEFAULT);

            System.out.println("JSONARRAY DO REPORT ----> " + jsonArray);

            if (jsonArray != null) {
                report.put("points", jsonArray.toString());
            } else {
                report.put("lat", latLng.latitude);
                report.put("lng", latLng.longitude);
            }

            report.put("img", base64Img);
            //report.put("report_thumbnail", base64Thumbnail);
            report.put("imgheight", h);
            report.put("imgwidth", w);
            report.put("address", mAddress);
            report.put("isprivate", activity.mIsPrivate);
            report.put("city", mDistrict);
            report.put("locality", mLocality);
            report.put("imgorientation", mOrientation);
            report.put("category", mCategory);


            if (activity.mReportType.equals("medium")) {

                report.put("title", mTitle);
                report.put("gravity", mGravity);


            } else if (activity.mReportType.equals("detailed")) {

                report.put("title", mTitle);
                report.put("gravity", mGravity);
                report.put("description", mDescription);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(context);

        url = URL + "/report/create";

        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("OK REPORTAR: " + response);
                        activity.setResult(Activity.RESULT_OK, new Intent());
                        int reportNum = Integer.parseInt(sharedPref.getString("user_reportNum", "0"));
                        sharedPref.edit().putString("user_reportNum", "" + (reportNum + 1)).apply();
                        Toast.makeText(context, "Your report has been registered!", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;

                        if (response != null) {

                            System.out.println("REPORT volley -> ERRO " + response.statusCode);
                            Toast.makeText(context, "Ups, failed to send report!", Toast.LENGTH_LONG).show();
                        } else {
                            System.out.println("REPORT volley -> ERRO Response veio null ");
                            Toast.makeText(context, "Your report has been registered!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(token, context);
            }

            @Override
            public byte[] getBody() {
                return report.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        setRetry(stringRequest);
        queue.add(stringRequest);

    }

    public static void changeRepStatusWIPRequest(String report, final Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);

        final SharedPreferences sharedPref = context.getSharedPreferences("Shared", Context.MODE_PRIVATE);
        final String mToken = sharedPref.getString("token", null);

        final String mReport = report;


        String url = URL + "/worker/wipreport/" + mReport;


        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("OK STATUS: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("ERRO DO REPORT CHANGE STATUS: " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(mToken, context);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                System.out.println("PARSE RESPONSE STATUS CODE --->>" + response);

                if (response.statusCode == 200) {
                    return Response.success("Report Status changed", HttpHeaderParser.parseCacheHeaders(response));
                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else if (response.statusCode == NO_CONTENT_ERROR) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }
        };
        setRetry(stringRequest);

        queue.add(stringRequest);
    }

    public static void changeRepStatusClosedRequest(String report, final Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);

        final SharedPreferences sharedPref = context.getSharedPreferences("Shared", Context.MODE_PRIVATE);
        final String mToken = sharedPref.getString("token", null);

        final String mReport = report;


        String url = URL + "/report/close/" + mReport;


        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("OK STATUS: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("ERRO DO REPORT CHANGE STATUS: " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(mToken, context);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                System.out.println("PARSE RESPONSE STATUS CODE --->>" + response);

                if (response.statusCode == 200) {
                    return Response.success("Report Status changed", HttpHeaderParser.parseCacheHeaders(response));
                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else if (response.statusCode == NO_CONTENT_ERROR) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }
        };
        setRetry(stringRequest);

        queue.add(stringRequest);
    }

    public static void postCommentRequest(final String report, String comment,
                                          final Context context,
                                          final MarkerActivity activity) {

        final String mComment = comment;
        final String mReport = report;

        final SharedPreferences sharedPref = context.getSharedPreferences("Shared", Context.MODE_PRIVATE);
        final String token = sharedPref.getString("token", null);

        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("text", mComment);

        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(context);

        url = URL + "/report/comment/post/" + mReport;

        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("OK COMMENT");
                        Toast.makeText(context, "Comment added!", Toast.LENGTH_LONG).show();
                        activity.marker_comment.setText("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        if (response != null) {

                            System.out.println("POST COMMENT volley -> ERRO " + response.statusCode);

                            Log.e("COMMENT volley -> ERRO ", "" + response.statusCode);

                            if (response.statusCode == 403) {
                                Toast.makeText(context, "No permission to comment!", Toast.LENGTH_LONG).show();
                                System.out.println("POST COMMENT volley -> User sem permissao para comentar " + response.statusCode);
                            } else

                                Toast.makeText(context, "Ups, failed to comment report!", Toast.LENGTH_LONG).show();
                        } else {
                            System.out.println("ADD NOTE volley -> ERRO Response veio null ");
                            Toast.makeText(context, "Ups, something went wrong!", Toast.LENGTH_LONG).show();
                        }


                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(token, context);
            }

            @Override
            public byte[] getBody() {
                return jsonObject.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        setRetry(stringRequest);
        queue.add(stringRequest);

    }

    public static void registerRequest(String username, String password, String email,
                                       final Context context,
                                       final RegisterActivity activity) {

        final String mUsernameRequest = username;
        final String mPasswordRequest = password;
        final String mEmailRequest = email;

        final JSONObject credentials = new JSONObject();

        try {
            credentials.put("username", mUsernameRequest);
            credentials.put("email", mEmailRequest);
            credentials.put("password", mPasswordRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(context);

        url = URL + "/register/user";

        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Toast.makeText(context, "User successfully registered!", Toast.LENGTH_LONG).show();
                        activity.onResponseCorrect();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        if (response != null) {

                            System.out.println("REGISTER volley -> ERRO " + response.statusCode);

                            if (response.statusCode == activity.CONFLICT_ERROR) {

                                Toast.makeText(context, "Username already exists", Toast.LENGTH_LONG).show();
                                activity.changeVisibility("Username");
                                activity.mUsername.setError("Choose a different username");
                                activity.mUsername.requestFocus();

                            } else {
                                Toast.makeText(context, "Ups, something went wrong!", Toast.LENGTH_LONG).show();
                            }

                            activity.showProgress(false);

                        } else
                            System.out.println("REGISTER volley -> ERRO Response veio a null");

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders("", context);
            }

            @Override
            public byte[] getBody() {
                return credentials.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        setRetry(stringRequest);
        queue.add(stringRequest);

    }

    public static void confirmRequest(String code, final Context context,
                                      final ProfileActivity activity) {

        final SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);
        final String mCode = code;
        final String mToken = sharedPref.getString("token", "");
        final String username = sharedPref.getString("username", "ERROR");

        final JSONObject credentials = new JSONObject();

        try {
            credentials.put("code", mCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        url = URL + "/profile/activate/" + username;

        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("OK CONFIRM ACCOUNT: " + response);

                        SharedPreferences.Editor editor = sharedPref.edit();

                        editor.putString("isConfirmed", "true");

                        editor.apply();

                        activity.checkIfAccountConfirmed();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        if (response != null) {
                            System.out.println("CONFIRM ACCOUNT volley -> ERRO " + response);
                        } else
                            System.out.println("CONFIRM ACCOUNT volley -> ERRO Response veio a null");


                        Toast.makeText(context, "Error confirming your account!", Toast.LENGTH_LONG).show();

                    }
                }
        ) {
            @Override
            public byte[] getBody() {
                return credentials.toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(mToken, context);
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        setRetry(stringRequest);

        activity.queue.add(stringRequest);
    }

    public static void userAvatarRequest(String username, MarkerClass marker, TaskClass task,
                                         final Context mContext) {

        System.out.println("Fiz avatar request para " + username);

        final String mUsername = username;

        final MarkerClass mMarker = marker;

        final TaskClass mTask = task;

        final SharedPreferences sharedPref = mContext.getSharedPreferences("Shared", Context.MODE_PRIVATE);
        final String token = sharedPref.getString("token", null);

        ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            // If no connectivity, cancel task and update Callback with null data.
            Toast.makeText(mContext, "No Internet Connection!", Toast.LENGTH_LONG).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(mContext);

        String url = URL + "/profile/getprofilepic/" + mUsername;

        jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        System.out.println("OK AVATAR: " + mUsername + " " + response);
                        try {
                            if (response.has("profpic")) {
                                String base64 = response.getString("profpic");
                                byte[] data = Base64.decode(base64, Base64.DEFAULT);

                                MapActivity.userAvatarMap.put(mUsername, data);

                                if (mMarker != null) {
                                    mMarker.makeAvatar(data);
                                    // markerAdapter.notifyItemChanged(position);

                                } else if (mTask != null) {
                                    mTask.makeAvatar(data);
                                    // taskAdapter.notifyItemChanged(position);
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        if (response != null) {
                            System.out.println("AVATAR volley -> ERRO " + response + " " + mUsername);
                        } else
                            System.out.println("AVATAR volley -> ERRO Response veio a null " + mUsername);

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(token, mContext);
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

                if (response.statusCode == 200) {

                    try {
                        String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        JSONObject jsonobject = new JSONObject(json);

                        return Response.success(jsonobject, HttpHeaderParser.parseCacheHeaders(response));

                    } catch (Exception e) {
                        e.printStackTrace();
                        return Response.error(new VolleyError(String.valueOf(response.statusCode)));
                    }
                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                1,  // maxNumRetries = 0 means no retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonRequest);

    }

    public static void loginRequest(String username, String password, final Context context,
                                    final LoginActivity activity) {

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            // If no connectivity, cancel task and update Callback with null data.
            activity.showProgress(false);
            Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_LONG).show();
            return;
        }

        final String mUsernameRequest = username;
        final String mPasswordRequest = password;

        final JSONObject credentials = new JSONObject();

        try {
            credentials.put("username", mUsernameRequest);
            credentials.put("password", mPasswordRequest);
            credentials.put("firebasetoken", "abc");
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(context);

        url = URL + "/login";

        jsonRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        System.out.println("OK LOGIN: " + response);

                        SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();

                        try {
                            editor.putString("token", response.getString("token"));

                            editor.putString("username", mUsernameRequest);

                            if (response.has("activated"))
                                editor.putString("isConfirmed", response.getString("activated"));

                            if ((response.getString("level")).contains("LEVEL")) {
                                editor.putString("userLevel", response.getString("level"));
                                editor.putString("userRole", "USER");
                            } else {
                                editor.putString("userRole", response.getString("level"));

                                if (response.has("Org")) {
                                    editor.putString("org_name", response.getString("Org"));
                                    System.out.println("NOME DA ORG" + (response.getString("Org")));
                                }
                            }

                            editor.apply();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        String level = sharedPref.getString("userRole", "");
                        if (level.equals("USER"))
                            profileRequest(sharedPref.getString("username", ""), context, activity, true);
                        else {
                            if (level.equals("WORKER")) {
                                activity.startActivity(new Intent(activity, MapActivity.class));
                                activity.finish();
                            } else {
                                Toast.makeText(context, "Invalid account type", Toast.LENGTH_LONG).show();
                                activity.showProgress(false);
                            }

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        if (response != null) {
                            System.out.println("LOGIN volley -> ERRO " + response + " " + error.getMessage());

                            if (response.statusCode == 403) {
                                activity.mPasswordView.setError(context.getString(R.string.error_incorrect_passwordl));
                                activity.mPasswordView.requestFocus();
                            }
                        }
                        Toast.makeText(context, "Ups something went wrong!", Toast.LENGTH_LONG).show();

                        System.out.println("REGISTER volley -> ERRO Response veio a null");

                        activity.showProgress(false);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders("", context);
            }

            @Override
            public byte[] getBody() {
                return credentials.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

                if (response.statusCode == 200) {

                    JSONObject result = new JSONObject();

                    try {
                        result.put("token", response.headers.get("Authorization"));

                        result.put("activated", response.headers.get("Activated"));

                        result.put("level", response.headers.get("Level"));

                        if (response.headers.get("Org") != null) {
                            result.put("Org", response.headers.get("Org"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }
        };
        setRetry(jsonRequest);

        queue.add(jsonRequest);

    }

    public static void authRequest(String token, final Context context,
                                   final LaunchActivity activity) {

        final String mToken = token;

        RequestQueue queue = Volley.newRequestQueue(context);

        url = URL + "/verifytoken";

        stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("TOKEN VALIDO");
                        SharedPreferences sharedPref = context.getSharedPreferences("Shared", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();

                        if (response.contains("LEVEL")) {
                            editor.putString("userRole", "USER");
                            editor.putString("userLevel", response);
                        } else
                            editor.putString("userRole", response);

                        editor.apply();

                        String level = sharedPref.getString("userRole", "");
                        if (level.equals("USER"))
                            profileRequest(sharedPref.getString("username", ""), context, activity, true);
                        else {
                            activity.startActivity(new Intent(activity, MapActivity.class));
                            activity.finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("REPORT volley -> ERRO - TOKEN INVALIDO");
                        Intent intent = new Intent(activity, LoginActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(mToken, context);
            }

            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                if (response.statusCode == 200) {

                    String result = response.headers.get("Level");

                    return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));

                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }
        };
        setRetry(stringRequest);

        queue.add(stringRequest);
    }

    public static void profileRequest(String username, Context context, final Activity activity, boolean isCurrentUser) {

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            // If no connectivity, cancel task and update Callback with null data.
            //activity.showProgress(false);
            Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_LONG).show();
            return;
        }

        final String mUsername = username;
        final Context mContext = context;
        final boolean currentUser = isCurrentUser;

        SharedPreferences sharedPref = mContext.getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);
        final String token = sharedPref.getString("token", "");

        RequestQueue queue = Volley.newRequestQueue(context);

        url = URL + "/profile/view/" + mUsername;

        jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        System.out.println("OK PROFILE: " + response);

                        SharedPreferences sharedPref = mContext.getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();

                        if (!currentUser) {
                            sharedPref = mContext.getApplicationContext().getSharedPreferences("SecondProfile", MODE_PRIVATE);
                            editor = sharedPref.edit();
                            editor.putString("username", mUsername);
                            editor.putString("isConfirmed", "true");
                        }

                        try {
                            editor.putString("user_points", String.valueOf(response.getInt("points")));
                            editor.putString("user_reportNum", String.valueOf(response.getInt("reports")));
                            editor.putString("user_email", response.getString("email"));
                            editor.putString("userLevel", response.getString("level"));

                            if (response.has("name"))
                                editor.putString("user_name", response.getString("name"));
                            else
                                editor.putString("user_name", mUsername);

                            if (response.has("birth") && !response.getString("birth").equals("  ")) {

                                String[] tokens = response.getString("birth").split(" ");
                                editor.putString("user_day", tokens[0]);
                                editor.putString("user_month", tokens[1]);
                                editor.putString("user_year", tokens[2]);
                            } else {
                                editor.putString("user_day", "");
                                editor.putString("user_month", "");
                                editor.putString("user_year", "");
                            }

                            if (response.has("sendemail"))
                                editor.putBoolean("sendemail", response.getBoolean("sendemail"));


                            if (response.has("locality"))
                                editor.putString("user_locality", response.getString("locality"));
                            else
                                editor.putString("user_locality", "");


                            if (response.has("phone")) {
                                editor.putString("user_phone", response.getString("phone"));
                            } else
                                editor.putString("user_phone", "");


                            if (response.has("address")) {
                                editor.putString("user_address", response.getString("address"));
                            } else
                                editor.putString("user_address", "");


                            if (response.has("gender")) {
                                editor.putString("user_gender", response.getString("gender"));
                            } else
                                editor.putString("user_gender", "");

                            if (response.has("job")) {
                                editor.putString("user_job", response.getString("job"));
                            } else
                                editor.putString("user_job", "");

                            if (response.has("skills")) {
                                editor.putString("user_skills", response.getString("skills"));
                            } else
                                editor.putString("user_skills", "");

                            if (response.has("profpic")) {
                                editor.putString("Avatar", response.getString("profpic"));
                            } else
                                editor.putString("Avatar", "");

                            editor.putString("askForProfile", "YES");

                            editor.apply();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (currentUser) {
                            activity.startActivity(new Intent(activity, MapActivity.class));
                            activity.finish();
                        }
                        else {
                            Intent intent = new Intent(activity, ProfileActivity.class);
                            intent.putExtra("isCurrentUser", false);
                            activity.startActivity(intent);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        if (response != null) {

                            System.out.println("PROFILE volley -> ERRO " + response + " " + response.statusCode);
                        } else
                            System.out.println("PROFILE volley -> ERRO Response veio null ");

                        SharedPreferences sharedPref = mContext.getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("user_day", "");
                        editor.putString("user_month", "");
                        editor.putString("user_year", "");
                        editor.putString("user_locality", "");
                        editor.putString("user_phone", "");
                        editor.putString("user_address", "");
                        editor.putString("user_gender", "");
                        editor.putString("user_job", "");
                        editor.putString("user_skills", "");
                        editor.putString("Avatar", "");
                        editor.putString("user_name", "");

                        //activity.showProgress(false);
                        editor.putString("askForProfile", "YES");
                        editor.apply();

                        activity.startActivity(new Intent(activity, MapActivity.class));
                        activity.finish();
                    }
                }
        ) {

            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(token, mContext);
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

                if (response.statusCode == 200) {

                    JSONObject result;

                    try {
                        String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        result = new JSONObject(json);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        result = new JSONObject();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        result = new JSONObject();
                    }

                    return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }
        };
        setRetry(jsonRequest);

        queue.add(jsonRequest);
    }

    public static void votesRequest(final String username, final String cursor,
                                    final Context context, final MapActivity activity) {

        final String mUsername = username;
        final String mCursor = cursor;

        // RequestQueue queue = Volley.newRequestQueue(context);
        final SharedPreferences sharedPref = context.getSharedPreferences("Shared", Context.MODE_PRIVATE);
        final String token = sharedPref.getString("token", null);

        url = URL + "/profile/votes/" + mUsername + "?cursor=" + mCursor;

        arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        System.out.println("VOTES OK: " + response);

                        if (mIsFinish.equals("FINISHED")) {
                            System.out.println("ACABARAM OS VOTOS");

                            activity.setUserVotes(response);
                        } else {
                            System.out.println("Continuar a pedir votos...");
                            activity.votesRequest(mUsername, mCursor);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("VOTES  volley -> ERRO " + error.toString());

                        if (error.toString().equals("com.android.volley.VolleyError: 204")) {
                            Toast.makeText(context, "No reports to show in this area!", Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(token, context);
            }

            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {

                System.out.println("PARSE RESPONSE STATUS CODE --->>" + response.statusCode);

                if (response.statusCode == 200) {

                    try {

                        if (response.headers.get("Cursor") != null)
                            mIsFinish = response.headers.get("Cursor");
                        else
                            mIsFinish = "FINISHED";

                        String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                        JSONArray jsonArray = new JSONArray(json);

                        return Response.success(jsonArray, HttpHeaderParser.parseCacheHeaders(response));

                    } catch (Exception e) {
                        e.printStackTrace();

                        return Response.error(new VolleyError(String.valueOf(response.statusCode)));
                    }

                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else if (response.statusCode == NO_CONTENT_ERROR) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }
        };
        setRetry(arrayRequest);

        activity.queue.add(arrayRequest);
    }

    public static void addNoteRequest(String note, String idTask, final Context context,
                                      final NoteActivity activity) {

        final String mNote = note;
        final String mIdTask = idTask;

        final SharedPreferences sharedPref = context.getSharedPreferences("Shared", Context.MODE_PRIVATE);
        final String token = sharedPref.getString("token", null);

        final JSONObject report = new JSONObject();

        try {
            report.put("note", mNote);

        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(context);

        url = URL + "/task/addnote/" + mIdTask;

        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("OK ADD NOTE: " + response);
                        Toast.makeText(context, "Note Added!", Toast.LENGTH_LONG).show();
                        activity.recreate();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        if (response != null) {

                            System.out.println("ADD NOTE volley -> ERRO " + response.statusCode);

                            if (response.statusCode == BAD_REQUEST_ERROR) {
                            } else {
                                Toast.makeText(context, "Ups, failed to send note!", Toast.LENGTH_LONG).show();

                            }
                        } else
                            System.out.println("ADD NOTE volley -> ERRO Response veio null ");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(token, context);
            }

            @Override
            public byte[] getBody() {
                return report.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        setRetry(stringRequest);
        queue.add(stringRequest);

    }

    public static void changePasswordRequest(String oldpass, String newpass,
                                             final Context context, View view,
                                             EditText oldPass, android.support.v7.app.AlertDialog alert) {

        final View mView = view;
        final EditText mOldPassEditText = oldPass;
        final String mOldPass = oldpass;
        final String mNewPass = newpass;
        final android.support.v7.app.AlertDialog mAlert = alert;
        final SharedPreferences sharedPref = context.getSharedPreferences("Shared", Context.MODE_PRIVATE);
        final String token = sharedPref.getString("token", null);

        final JSONObject passwordJson = new JSONObject();

        try {
            passwordJson.put("oldpassword", mOldPass);
            passwordJson.put("newpassword", mNewPass);

        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(context);

        url = URL + "/profile/changepassword";

        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("OK CHANGE PASSWORD: " + response);
                        mAlert.dismiss();
                        Toast.makeText(context, "Password Changed!", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        if (response != null) {

                            System.out.println("CHANGE PASSWORD volley -> ERRO " + response.statusCode);

                            if (response.statusCode == BAD_REQUEST_ERROR) {
                            } else {
                                Toast.makeText(context, "Ups, could not change the password!", Toast.LENGTH_LONG).show();

                            }
                            mOldPassEditText.setError("Invalid password");
                            mView.requestFocus();
                        } else
                            System.out.println("CHANGE PASSWORD volley -> ERRO Response veio a null");

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(token, context);
            }

            @Override
            public byte[] getBody() {
                return passwordJson.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        setRetry(stringRequest);
        queue.add(stringRequest);

    }

    public static void logoutRequest(String token, final Context context,
                                     final Activity activity, int request) {

        final String mToken = token;

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = "";

        if (request == L_EVERYWHERE)
            url = URL + "/logout/everywhere";
        if (request == L_ONCE)
            url = URL + "/logout";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // resp
                        SharedPreferences sharedPref = context.getSharedPreferences("Shared", MODE_PRIVATE);
                        sharedPref.edit().remove("token").commit();

                        Toast.makeText(context, "User Logged Out", Toast.LENGTH_LONG).show();

                        Intent intents = new Intent(activity, LoginActivity.class);
                        intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(intents);
                        activity.finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("LOGOUT volley -> ERRO " + error);
                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                System.out.println("TOKEN NO LOGOUT: " + mToken);

                return setHeaders(mToken, context);
            }

        };
        setRetry(stringRequest);

        queue.add(stringRequest);
    }

    public static void changeProfPicRequest(String avatar, final Context context) {

        final String mAvatar = avatar;
        final SharedPreferences sharedPref = context.getSharedPreferences("Shared", Context.MODE_PRIVATE);
        final String token = sharedPref.getString("token", null);
        final JSONObject profilePic = new JSONObject();

        try {
            profilePic.put("pic", mAvatar);
            profilePic.put("orientation", 0);

        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(context);

        url = URL + "/profile/changeprofilepic";

        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("OK Change profile: " + response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        if (response != null) {

                            System.out.println("CHANGE PROFILE PIC volley -> ERRO " + response.statusCode);

                            if (response.statusCode == BAD_REQUEST_ERROR) {
                            } else {
                                Toast.makeText(context, "Ups, failed to change profile picture!", Toast.LENGTH_LONG).show();

                            }
                        } else
                            System.out.println("CHANGE PROFILE PIC volley -> ERRO Response veio a null");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(token, context);
            }

            @Override
            public byte[] getBody() {
                return profilePic.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        setRetry(stringRequest);
        queue.add(stringRequest);

    }

    public static void editProfileRequest(String phone, String name, String gender, String
            address,
                                          String locality, String zip, String day, String month, String year, String job,
                                          String skills, String username, final Context context,
                                          final ProfileActivity activity) {

        final String mPhone = phone;
        final String mName = name;
        final String mGender = gender;
        final String mAddress = address;
        final String mLocality = locality;
        final String mZip = zip;
        final String mDay = day;
        final String mMonth = month;
        final String mYear = year;
        final String mJob = job;
        final String mSkills = skills;
        final String mUsername = username;

        final SharedPreferences sharedPref = context.getSharedPreferences("Shared", Context.MODE_PRIVATE);
        final String mToken = sharedPref.getString("token", null);

        final JSONObject json = new JSONObject();

        try {
            json.put("phone", mPhone);
            json.put("name", mName);
            json.put("gender", mGender);
            json.put("address", mAddress);
            json.put("locality", mLocality);
            json.put("zip", mZip);
            json.put("birth", mDay + " " + mMonth + " " + mYear);
            json.put("job", mJob);
            json.put("skills", mSkills);

        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(context);

        url = URL + "/profile/update/" + mUsername;

        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("OK EDIT PROFILE: " + response);

                        Toast.makeText(context, "Your profile has been successfully edited", Toast.LENGTH_LONG).show();

                        SharedPreferences.Editor editor = sharedPref.edit();

                        editor.putString("user_phone", mPhone);
                        editor.putString("user_name", mName);
                        editor.putString("user_gender", mGender);
                        editor.putString("user_address", mAddress);
                        editor.putString("user_locality", mLocality);
                        editor.putString("user_zip", mZip);
                        editor.putString("user_day", mDay);
                        editor.putString("user_month", mMonth);
                        editor.putString("user_year", mYear);
                        editor.putString("user_job", mJob);
                        editor.putString("user_skills", mSkills);
                        editor.apply();

                        activity.recreate();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        if (response != null) {

                            System.out.println("EDIT PROFILE volley -> ERRO " + response.statusCode + "  " + error);

                            Log.e("Edit volley -> ERRO ", "" + response.statusCode);

                            Toast.makeText(context, "Ups, error editing profile!", Toast.LENGTH_LONG).show();

                        } else
                            System.out.println("EDIT PROFILE volley -> ERRO Response veio a null");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders(mToken, context);
            }

            @Override
            public byte[] getBody() {
                return json.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        setRetry(stringRequest);
        queue.add(stringRequest);

    }

    public static void forgotPasswordRequest(String email, final Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);

        final String mEmail = email;


        String url = URL + "/profile/forgotpassword/" + mEmail;


        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("OK STATUS: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("ERRO DO REPORT CHANGE STATUS: " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                return setHeaders("", context);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                System.out.println("PARSE RESPONSE STATUS CODE --->>" + response);

                if (response.statusCode == 200) {
                    return Response.success("Recover Password Request Sent", HttpHeaderParser.parseCacheHeaders(response));
                } else if (response.statusCode == 403) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else if (response.statusCode == NO_CONTENT_ERROR) {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                } else {
                    VolleyError error = new VolleyError(String.valueOf(response.statusCode));
                    return Response.error(error);
                }
            }
        };
        setRetry(stringRequest);

        queue.add(stringRequest);
    }

    private static void setRetry(Request request) {

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 5,
                -1,  // maxNumRetries = 0 means no retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private static Map<String, String> setHeaders(String mToken, Context mContext) {
        Map<String, String> params = new HashMap<String, String>();
        if (!mToken.equals(""))
            params.put("Authorization", mToken);

        params.put("Device-ID", Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID));
        System.out.println("DEVICE ID: " + Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID));
        params.put("Device-App", "Android");
        System.out.println("Device-Info: " + android.os.Build.MODEL + " " + Build.BRAND);
        params.put("Device-Info", Build.BRAND + " " + Build.MODEL);

        return params;
    }

}