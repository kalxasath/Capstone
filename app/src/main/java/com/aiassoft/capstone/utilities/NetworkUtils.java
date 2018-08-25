package com.aiassoft.capstone.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {

    private static final String LOG_TAG = MyApp.APP_TAG + NetworkUtils.class.getSimpleName();

    private NetworkUtils() {
        throw new AssertionError(R.string.no_instances_for_you);
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        }
        catch (FileNotFoundException e) {
            Log.d(LOG_TAG, MyApp.getContext().getString(R.string.invalid_api_key));
            e.printStackTrace();
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            urlConnection.disconnect();
        }
    }

    /**
     *
     * @return TRUE, if we are connected to the internet, otherwise returns FALSE
     */
    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) MyApp.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


}
