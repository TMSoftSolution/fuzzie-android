package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import sg.com.fuzzie.android.api.models.JackpotResult;

/**
 * Created by joma on 10/17/17.
 */

public class JackpotResults {

    @SerializedName("results")
    private List<JackpotResult> results;

    public List<JackpotResult> getResults() {
        return results;
    }

    public void setResults(List<JackpotResult> results) {
        this.results = results;
    }
}
