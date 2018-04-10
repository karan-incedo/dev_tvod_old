package com.viewlift.models.data.appcms.subscribeForLatestNewsPojo;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class Stats{

	@SerializedName("avg_click_rate")
	private int avgClickRate;

	@SerializedName("avg_open_rate")
	private int avgOpenRate;

	public void setAvgClickRate(int avgClickRate){
		this.avgClickRate = avgClickRate;
	}

	public int getAvgClickRate(){
		return avgClickRate;
	}

	public void setAvgOpenRate(int avgOpenRate){
		this.avgOpenRate = avgOpenRate;
	}

	public int getAvgOpenRate(){
		return avgOpenRate;
	}

	@Override
 	public String toString(){
		return 
			"Stats{" + 
			"avg_click_rate = '" + avgClickRate + '\'' + 
			",avg_open_rate = '" + avgOpenRate + '\'' + 
			"}";
		}
}