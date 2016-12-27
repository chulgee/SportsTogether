package com.iron.dragon.sportstogether.http.retropit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by P16018 on 2016-12-27.
 */

public interface CallbackWithExists<T> extends Callback<T> {
    public void onExists(Call<T> call, Response<T> response);
}
