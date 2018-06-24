package com.wokesolutions.ignes.ignes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class RequestsVolley {

    private static final int SERVER_ERROR = 500;
    private static final int NO_CONTENT_ERROR = 204;
    private static final int NOT_FOUND_ERROR = 404;
    private static final int BAD_REQUEST_ERROR = 400;

    private static final int L_EVERYWHERE = 1;
    private static final int L_ONCE = 0;

    private static StringRequest stringRequest;
    private static JsonObjectRequest jsonRequest;
    private static JsonArrayRequest arrayRequest;
    private static String url, mIsFinish;


    public static void thumbnailRequest(String reportId, MarkerClass marker, final int position, final Context mContext, final MarkerAdapter markerAdapter,
                                        TaskClass task, final TaskAdapter taskAdapter) {

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

        String url = "https://hardy-scarab-200218.appspot.com/api/report/thumbnail/" + report;

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        System.out.println("OK: " + response);
                        try {

                            String base64 = response.getString("report_thumbnail");
                            byte[] data = Base64.decode(base64, Base64.DEFAULT);

                            if (mMarker != null) {
                                mMarker.makeImg(data);
                                markerAdapter.notifyItemChanged(position);

                            } else if (mTask != null) {
                                mTask.makeImg(data);
                                taskAdapter.notifyItemChanged(position);
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
                        System.out.println("THUMBNAIL volley -> ERRO " + response);


                    }
                }
        ) {
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

    public static void mapRequest(double lat, double lng, int radius, String token, String cursor, final Context context, final MapActivity activity) {

        final double mLat = lat;
        final double mLng = lng;
        final int mRadius = radius;
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
            url = "https://hardy-scarab-200218.appspot.com/api/report/getwithinradius?"
                    + "lat=" + activity.mCurrentLocation.getLatitude() + "&lng=" + activity.mCurrentLocation.getLongitude()
                    + "&radius=" + 5 + "&cursor=" + mCursor;
        } else if (activity.mRole.equals("WORKER")) {
            url = "https://hardy-scarab-200218.appspot.com/api/worker/tasks/" + activity.mUsername + "?cursor=" + mCursor;
        }

        arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // response
                        System.out.println("OK: " + response);

                        activity.isReady = true;

                        System.out.println("RESPONSE DATA: ->>> " + response);
                        if (activity.mRole.equals("USER"))
                            activity.setMarkers(response, mLat, mLng, mLocality);
                        else if (activity.mRole.equals("WORKER"))
                            activity.setWorkerMarkers(response, mLat, mLng, mLocality);

                        if (mIsFinish.equals("FINISHED")) {
                            System.out.println("ACABARAM OS REPORTS");
                            activity.votesRequest(activity.mUsername, "");
                        } else {
                            System.out.println("Continuar a pedir...");
                            activity.mapRequest(mLat, mLng, 10000, mToken, mIsFinish);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("ERRO DO MAP: " + error.toString());

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
                Map<String, String> params = new HashMap<String, String>();
                if (activity.mRole.equals("WORKER"))
                    params.put("Authorization", mToken);

                return params;
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

                        System.out.println("RESPONSE HERE ->>> " + jsonArray);

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

    public static void userReportsRequest(String username, String token, String cursor, final Context context, final ProfileActivity activity) {

        final String mUsername = username;
        final String mToken = token;
        final String mCursor = cursor;
        final Map<String, MarkerClass> userMap;


        String url = "https://hardy-scarab-200218.appspot.com/api/profile/reports/" + mUsername + "?cursor="+mCursor;


        arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // response
                        System.out.println("OK: " + response);

                        System.out.println("RESPONSE DATA: ->>> " + response);


                        if (mIsFinish.equals("FINISHED")) {
                            System.out.println("ACABARAM OS REPORTS");

                            activity.setUserMap(response);
                            activity.markerAdapter = new MarkerAdapter(context, activity.markerMap);
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

                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", mToken);
                return params;
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

                        System.out.println("CURSOR DE AHBFAHDB: " + mIsFinish);

                        String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                        JSONArray jsonArray = new JSONArray(json);

                        System.out.println("RESPONSE HERE ->>> " + jsonArray);

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

    public static void taskNotesRequest(String taskID, String token, String cursor, final Context context, final NoteActivity activity) {

        final String mTask = taskID;
        final String mToken = token;
        final String mCursor = cursor;


        String url = "https://hardy-scarab-200218.appspot.com/api/task/notes/" + mTask;


        arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // response
                        System.out.println("OK: " + response);

                        System.out.println("RESPONSE DATA: ->>> " + response);

                        if (mIsFinish.equals("FINISHED")) {
                            System.out.println("ACABARAM OS REPORTS");

                            activity.setNotesList(response);
                        } else {
                            System.out.println("Continuar a pedir...");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("ERRO DO USER REPORTS: " + error.toString());

                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", mToken);
                return params;
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

                        System.out.println("RESPONSE HERE ->>> " + jsonArray);

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

    public static void sendAllVotesRequest(JSONArray votes, String token, final Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);

        final JSONArray mVotes = votes;
        final String mToken = token;


        String url = "https://hardy-scarab-200218.appspot.com/api/report/vote/multiple";


        arrayRequest = new JsonArrayRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // response
                        System.out.println("OK: " + response);

                        System.out.println("RESPONSE DATA: ->>> " + response);

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
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", mToken);
                return params;
            }

            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {

                System.out.println("PARSE RESPONSE STATUS CODE --->>" + response.statusCode);

                if (response.statusCode == 200) {


                    System.out.println("RESPONSE HERE ->>> " + response);

                    return Response.success(new JSONArray(), HttpHeaderParser.parseCacheHeaders(response));


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
                System.out.println("WOOOOOW "+mVotes.toString());
                return mVotes.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        setRetry(arrayRequest);

        queue.add(arrayRequest);
    }

    public static void reportRequest(byte[] thumbnail, String description, String title, String district, String address,
                                     String locality, int gravity, double lat, double lng, int orientation, final Context context,
                                     final ReportFormActivity activity) {

        //final byte[] mThumbnail = thumbnail;
        final double mLat = lat;
        final double mLng = lng;
        final String base64Img;
        // final String base64Thumbnail = Base64.encodeToString(mThumbnail, Base64.DEFAULT);
        final String mDescription = description;
        final int mGravity = gravity;
        final String mTitle = title;
        final String mDistrict = district;
        final String mAddress = address;
        final String mLocality = locality;
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

            System.out.println("BYTE COUNT IMG: " + bitmap.getByteCount());
            System.out.println("BYTEARRAY ENVIADO DA IMG: " + activity.imgByteArray.length);

            report.put("report_lat", mLat);
            report.put("report_lng", mLng);
            report.put("report_img", base64Img);
            //report.put("report_thumbnail", base64Thumbnail);
            report.put("report_imgheight", h);
            report.put("report_imgwidth", w);
            report.put("report_address", mAddress);
            report.put("report_private", activity.mIsPrivate);
            report.put("report_city", mDistrict);
            report.put("report_locality", mLocality);
            report.put("report_imgorientation", mOrientation);

            if (activity.mReportType.equals("medium")) {

                report.put("report_title", mTitle);
                report.put("report_gravity", mGravity);


            } else if (activity.mReportType.equals("detailed")) {

                report.put("report_title", mTitle);
                report.put("report_gravity", mGravity);
                report.put("report_description", mDescription);

            }

            System.out.println("REPORT JSON: " + report);
            System.out.println("ADDRESS DO DETAILED: " + mAddress + "Localidade e cidade " + mLocality + " " + mDistrict);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(context);

        url = "https://hardy-scarab-200218.appspot.com/api/report/create";

        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("OK: " + response);
                        activity.setResult(Activity.RESULT_OK, new Intent());
                        activity.finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        System.out.println("REPORT volley -> ERRO " + response.statusCode);

                        Log.e("REPORT volley -> ERRO ", "" + response.statusCode);

                        if (response.statusCode == BAD_REQUEST_ERROR)
                            activity.showProgress(false);
                        else
                            activity.showProgress(false);

                        Toast.makeText(context, "Ups, failed to send report!", Toast.LENGTH_LONG).show();

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", token);

                return params;
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

    public static void registerRequest(String username, String password, String email, final Context context,
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

        url = "https://hardy-scarab-200218.appspot.com/api/register/user";

        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Toast.makeText(context, "User successfully registered!", Toast.LENGTH_LONG).show();
                        activity.startActivity(new Intent(activity, LoginActivity.class));
                        activity.finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;

                        System.out.println("REGISTER volley -> ERRO " + response.statusCode);

                        if (response.statusCode == activity.CONFLICT_ERROR) {

                            Toast.makeText(context, "Username already exists", Toast.LENGTH_LONG).show();
                            activity.changeVisibility("Username");
                            activity.mUsername.setError("Choose a different username");
                            activity.mUsername.requestFocus();

                        } else {
                            Toast.makeText(context, "Ups, something went wrong!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        ) {
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

    public static void confirmRequest(String code, final Context context, final ProfileActivity activity) {

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

        url = "https://hardy-scarab-200218.appspot.com/api/profile/activate/" + username;

        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("OK: " + response);

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

                        System.out.println("CONFIRM ACCOUNT volley -> ERRO " + response);

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
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", mToken);

                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        setRetry(stringRequest);

        activity.queue.add(stringRequest);
    }

    public static void loginRequest(String username, String password, final Context context, final LoginActivity activity) {

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
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(context);

        url = "https://hardy-scarab-200218.appspot.com/api/login";

        jsonRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        System.out.println("OK: " + response);

                        SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();

                        try {
                            editor.putString("token", response.getString("token"));

                            editor.putString("username", mUsernameRequest);

                            if (response.has("activated"))
                                editor.putString("isConfirmed", response.getString("activated"));

                            System.out.println("LOGIIIN CENAS " + (response.getString("level")).contains("LEVEL") + " " + response.getString("level"));

                            if ((response.getString("level")).contains("LEVEL"))
                                editor.putString("userLevel", "USER");
                            else {
                                editor.putString("userLevel", response.getString("level"));

                                if (response.has("Org")) {
                                    editor.putString("org_name", response.getString("Org"));
                                    System.out.println("NOME DA ORG" + (response.getString("Org")));
                                }

                            }

                            editor.apply();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        activity.startActivity(new Intent(activity, MapActivity.class));
                        activity.finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        System.out.println("LOGIN volley -> ERRO " + response + " " + error);

                        if (response.statusCode == 403) {
                            activity.mPasswordView.setError(context.getString(R.string.error_incorrect_password));
                            activity.mPasswordView.requestFocus();
                        } else
                            Toast.makeText(context, "Ups something went wrong!", Toast.LENGTH_LONG).show();

                        activity.showProgress(false);
                    }
                }
        ) {
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

                        System.out.println("PRINT DO QUE RECEBES "+ response.headers);

                        if (response.headers.get("Org") != null) {
                            System.out.println("NOME DA ORG" + (response.headers.get("Org")));
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

    public static void authRequest(String token, final Context context, final LaunchActivity activity) {

        final String mToken = token;

        RequestQueue queue = Volley.newRequestQueue(context);

        url = "https://hardy-scarab-200218.appspot.com/api/verifytoken";

        stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("TOKEN VALIDO");
                        SharedPreferences sharedPref = context.getSharedPreferences("Shared", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        System.out.println("LOGIIIN CENAS " + response.contains("LEVEL"));

                        if (response.contains("LEVEL"))
                            editor.putString("userLevel", "USER");
                        else
                            editor.putString("userLevel", response);

                        editor.apply();

                        String level = sharedPref.getString("userLevel", "");
                        if (level.equals("USER"))
                            profileRequest(sharedPref.getString("username", ""), context, activity);
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
                        activity.startActivity(new Intent(activity, LoginActivity.class));
                        activity.finish();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", mToken);

                return params;
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

    public static void profileRequest(String username, Context context, final Activity activity) {

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
        SharedPreferences sharedPref = mContext.getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);
        final String token = sharedPref.getString("token", "");


        RequestQueue queue = Volley.newRequestQueue(context);

        url = "https://hardy-scarab-200218.appspot.com/api/profile/view/" + mUsername;

        jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        System.out.println("OK: " + response);

                        SharedPreferences sharedPref = mContext.getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();

                        try {
                            editor.putString("user_points", String.valueOf(response.getInt("userpoints_points")));
                            editor.putString("user_reportNum", String.valueOf(response.getInt("user_reports")));
                            editor.putString("user_email", response.getString("user_email"));

                            if (response.has("useroptional_name"))
                                editor.putString("user_name", response.getString("useroptional_name"));

                            if (response.has("useroptional_birth")) {
                                String[] tokens = response.getString("useroptional_birth").split(" ");

                                editor.putString("user_day", tokens[0]);
                                editor.putString("user_month", tokens[1]);
                                editor.putString("user_year", tokens[2]);
                            }

                            if (response.has("useroptional_locality"))
                                editor.putString("user_locality", response.getString("useroptional_locality"));

                            if (response.has("useroptional_phone")) {
                                editor.putString("user_phone", response.getString("useroptional_phone"));
                            }

                            if (response.has("useroptional_address")) {
                                editor.putString("user_address", response.getString("useroptional_address"));
                            }

                            if (response.has("useroptional_gender")) {
                                editor.putString("user_gender", response.getString("useroptional_gender"));
                            }

                            if (response.has("useroptional_job")) {
                                editor.putString("user_job", response.getString("useroptional_job"));
                            }
                            if (response.has("useroptional_skills")) {
                                editor.putString("user_skills", response.getString("useroptional_skills"));
                            }

                            editor.putString("askForProfile", "NO");

                            editor.apply();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        activity.startActivity(new Intent(activity, MapActivity.class));
                        activity.finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        System.out.println("PROFILE volley -> ERRO " + response + " " + response.statusCode);

                        SharedPreferences sharedPref = mContext.getApplicationContext().getSharedPreferences("Shared", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();


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
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", token);

                return params;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

                if (response.statusCode == 200) {

                    JSONObject result;

                    try {
                        String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        result = new JSONObject(json);
                        System.out.println("RESPONSE DO VIEW PROFILE NO PARSERESPONSE --->>> " + json);
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

    public static void votesRequest(final String username, final String cursor, final Context context, final MapActivity activity) {

        final String mUsername = username;
        final String mCursor = cursor;

        // RequestQueue queue = Volley.newRequestQueue(context);
        final SharedPreferences sharedPref = context.getSharedPreferences("Shared", Context.MODE_PRIVATE);
        final String token = sharedPref.getString("token", null);

        url = "https://hardy-scarab-200218.appspot.com/api/profile/votes/" + mUsername + "?cursor=" + mCursor;

        arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        System.out.println("OK: " + response);

                        if (mIsFinish.equals("FINISHED")) {
                            System.out.println("ACABARAM OS REPORTS");

                            activity.setUserVotes(response);
                        } else {
                            System.out.println("Continuar a pedir...");
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", token);

                return params;
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

                        System.out.println("RESPONSE HERE ->>> " + jsonArray);

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

    public static void addNoteRequest(String note, String idTask, final Context context, final NoteActivity activity) {

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

        url = "https://hardy-scarab-200218.appspot.com/api/task/addnote/" + mIdTask;

        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("OK: " + response);
                        Toast.makeText(context, "Note Added!", Toast.LENGTH_LONG).show();
                        activity.recreate();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        System.out.println("ADD NOTE volley -> ERRO " + response.statusCode);

                        if (response.statusCode == BAD_REQUEST_ERROR) {
                        } else {
                            Toast.makeText(context, "Ups, failed to send note!", Toast.LENGTH_LONG).show();

                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", token);

                return params;
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

    public static void changePasswordRequest(String oldpass, String newpass, final Context context, View view,
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

        url = "https://hardy-scarab-200218.appspot.com/api/profile/changepassword";

        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("OK: " + response);
                        mAlert.dismiss();
                        Toast.makeText(context, "Password Changed!", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        System.out.println("CHANGE PASSWORD volley -> ERRO " + response.statusCode);

                        if (response.statusCode == BAD_REQUEST_ERROR) {
                        } else {
                            Toast.makeText(context, "Ups, could not change the password!", Toast.LENGTH_LONG).show();

                        }
                        mOldPassEditText.setError("Invalid password");
                        mView.requestFocus();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", token);

                return params;
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

    public static void logoutRequest(String token, final Context context, final Activity activity, int request) {

        final String mToken = token;

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = "";

        if (request == L_EVERYWHERE)
            url = "https://hardy-scarab-200218.appspot.com/api/logout/everywhere";
        if (request == L_ONCE)
            url = "https://hardy-scarab-200218.appspot.com/api/logout";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // resp
                        SharedPreferences sharedPref = context.getSharedPreferences("Shared", MODE_PRIVATE);
                        sharedPref.edit().remove("token").commit();
                        System.out.println("User Logged Out");

                        activity.startActivity(new Intent(activity, LoginActivity.class));
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", mToken);

                return params;
            }

        };
        setRetry(stringRequest);

        queue.add(stringRequest);
    }

    public static void editProfileRequest(String phone, String name, String gender, String address,
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

            json.put("useroptional_phone", mPhone);
            json.put("useroptional_name", mName);
            json.put("useroptional_gender", mGender);
            json.put("useroptional_address", mAddress);
            json.put("useroptional_locality", mLocality);
            json.put("useroptional_zip", mZip);
            json.put("useroptional_birth", mDay + " " + mMonth + " " + mYear);
            json.put("useroptional_job", mJob);
            json.put("useroptional_skills", mSkills);

            System.out.println("JSON EDIT PROFILE ->  " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(context);

        url = "https://hardy-scarab-200218.appspot.com/api/profile/update/" + mUsername;

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
                        System.out.println("EDIT PROFILE volley -> ERRO " + response.statusCode + "  " + error);

                        Log.e("REPORT volley -> ERRO ", "" + response.statusCode);

                        Toast.makeText(context, "Ups, error editing profile!", Toast.LENGTH_LONG).show();

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", mToken);

                return params;
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

    private static void setRetry(Request request) {

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4,
                -1,  // maxNumRetries = 0 means no retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}