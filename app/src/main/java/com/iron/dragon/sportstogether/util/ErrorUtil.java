package com.iron.dragon.sportstogether.util;

import com.iron.dragon.sportstogether.retrofit.Error;
import com.iron.dragon.sportstogether.retrofit.GitHubService;
import com.iron.dragon.sportstogether.retrofit.Profile;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by chulchoice on 2016-11-18.
 */
public class ErrorUtil {
    public static Error parse(Response response){
        Converter<ResponseBody, Error> errorConverter = GitHubService.retrofit.responseBodyConverter(Error.class, new Annotation[0]);

        Error error = null;

        try {
            error = errorConverter.convert(response.errorBody());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return error;
    }
}
