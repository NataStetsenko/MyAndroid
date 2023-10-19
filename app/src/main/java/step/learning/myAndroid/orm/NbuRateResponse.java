package step.learning.myAndroid.orm;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class NbuRateResponse {
    private List<NbuRate> rates;
    public NbuRateResponse(JSONArray jsonArray) throws JSONException {
        rates = new ArrayList<>();
        int length = jsonArray.length();
        for (int i = 1; i < length; i++) {
            rates.add(new NbuRate(jsonArray.getJSONObject(i)));
        }
    }

    public List<NbuRate> getRates() {
        return rates;
    }

    public void setRates(List<NbuRate> rates) {
        this.rates = rates;
    }


}
