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
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class RequestsVolley {

    private static StringRequest stringRequest;
    private static JsonObjectRequest jsonRequest;
    private static String url;
    
    public static void reportRequest(byte[] thumbnail, String description, String title, String district, String address,
                                     String locality, int gravity, double lat, double lng, final Context context,
                                     final ReportFormActivity activity) {

        final byte[] mThumbnail = thumbnail;
        final double mLat = lat;
        final double mLng = lng;
        final String base64Img;
        final String base64Thumbnail = Base64.encodeToString(mThumbnail, Base64.DEFAULT);
        final String mDescription = description;
        final int mGravity = gravity;
        final String mTitle = title;
        final String mDistrict = district;
        final String mAddress = address;
        final String mLocality = locality;

        final SharedPreferences sharedPref = context.getSharedPreferences("Shared", Context.MODE_PRIVATE);
        final String token = sharedPref.getString("token", null);

        final JSONObject report = new JSONObject();

        try {
            InputStream imageStream = activity.getContentResolver().openInputStream(activity.mImageURI);

            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            ByteArrayOutputStream imgStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, imgStream);
            activity.imgByteArray = imgStream.toByteArray();
            base64Img = Base64.encodeToString(activity.imgByteArray, Base64.DEFAULT);

            System.out.println("BYTE COUNT IMG: " + bitmap.getByteCount());
            System.out.println("BYTEARRAY ENVIADO DA IMG: " + activity.imgByteArray.length);

            if (activity.mReportType.equals("fast")) {

                report.put("report_lat", mLat);
                report.put("report_lng", mLng);
                report.put("report_img", base64Img);
                report.put("report_thumbnail", base64Thumbnail);
                report.put("report_address", mAddress);
                report.put("report_city", mDistrict);
                report.put("report_locality", mLocality);
                report.put("report_private", activity.mIsPrivate);

            } else if (activity.mReportType.equals("medium")) {

                report.put("report_lat", mLat);
                report.put("report_lng", mLng);
                report.put("report_thumbnail", base64Thumbnail);
                report.put("report_img", base64Img);
                report.put("report_title", mTitle);
                report.put("report_gravity", mGravity);
                report.put("report_address", mAddress);
                report.put("report_city", mDistrict);
                report.put("report_locality", mLocality);
                report.put("report_private", activity.mIsPrivate);

            } else if (activity.mReportType.equals("detailed")) {

                report.put("report_lat", mLat);
                report.put("report_lng", mLng);
                report.put("report_thumbnail", base64Thumbnail);
                report.put("report_img", base64Img);
                report.put("report_title", mTitle);
                report.put("report_gravity", mGravity);
                report.put("report_description", mDescription);
                report.put("report_address", mAddress);
                report.put("report_city", mDistrict);
                report.put("report_locality", mLocality);
                report.put("report_private", activity.mIsPrivate);
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
                        System.out.println("ERRO DO LOGIN: " + response.statusCode);

                        if (response.statusCode == 400) {
                        } else {
                            Toast.makeText(context, "Ups your report went wrong!", Toast.LENGTH_LONG).show();
                            activity.showProgress(false);
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
                        System.out.println("RESPOSTA DO REGISTER: " + response.statusCode);

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

                        System.out.println("ERRO DO CONFIRM: " + response);

                        Toast.makeText(context, "Error with your confirmation!", Toast.LENGTH_LONG).show();

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
                            editor.putString("isConfirmed", response.getString("activated"));
                            System.out.println("LOGIIIN CENAS " + (response.getString("level")).contains("LEVEL"));
                            if ((response.getString("level")).contains("LEVEL"))
                                editor.putString("userLevel", "USER");
                            else
                                editor.putString("userLevel", response.getString("level"));

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
                        System.out.println("ERRO DO LOGIN: " + response);

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

                        activity.startActivity(new Intent(activity, MapActivity.class));
                        activity.finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("TOKEN INVALIDO");
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

    private static void setRetry(Request request) {

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                1,  // maxNumRetries = 0 means no retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}