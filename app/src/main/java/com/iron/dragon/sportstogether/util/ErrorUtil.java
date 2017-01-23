package com.iron.dragon.sportstogether.util;

import com.iron.dragon.sportstogether.http.Error;
import com.iron.dragon.sportstogether.http.retrofit.GitHubService;

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
        GitHubService.ServiceGenerator.changeApiBaseUrl(Const.MAIN_URL);
        Converter<ResponseBody, Error> errorConverter = GitHubService.ServiceGenerator.retrofit.responseBodyConverter(Error.class, new Annotation[0]);

        Error error = null;

        try {
            error = errorConverter.convert(response.errorBody());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return error;
    }
}
