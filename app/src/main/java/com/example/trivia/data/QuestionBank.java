package com.example.trivia.data;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.trivia.controller.AppController;
import com.example.trivia.model.Question;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.example.trivia.controller.AppController.TAG;

public class QuestionBank {

    private ArrayList<Question> questionArrayList = new ArrayList<>();
    private String url = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";

    public List<Question> getQuestions(final AnswerListAsyncResponse callBack){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                (JSONArray) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG,"onResponse: " + response);
                        for (int i =0; i < response.length(); i++){
                            try {
                                Question question = new Question(response.getJSONArray(i).getString(0), response.getJSONArray(i).getBoolean(1));
                                questionArrayList.add(question);
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
if(callBack != null){
    callBack.processFinished(questionArrayList);
}
                    }
                },
                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
        return questionArrayList;
    }

}
