package co.thenets.brisk.rest.responses;

import com.google.gson.annotations.SerializedName;

import co.thenets.brisk.models.Dashboard;

/**
 * Created by DAVID-WORK on 12/03/2016.
 */
public class GetDashboardResponse extends BaseResponse
{
    @SerializedName("dashboard")
    private Dashboard mDashboard;

    public Dashboard getDashboard()
    {
        return mDashboard;
    }

    @Override
    public String toString()
    {
        return "GetDashboard{" +
                "mDashboard=" + mDashboard +
                "} " + super.toString();
    }
}
