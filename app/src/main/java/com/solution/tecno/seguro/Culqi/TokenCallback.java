package com.solution.tecno.seguro.Culqi;

import org.json.JSONObject;

/**
 * Created by culqi on 2/7/17.
 */

public interface TokenCallback {

    void onSuccess(JSONObject token);

    void onError(Exception error);

}
