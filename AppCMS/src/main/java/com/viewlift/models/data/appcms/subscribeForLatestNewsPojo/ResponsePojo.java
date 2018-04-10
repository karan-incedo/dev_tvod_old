package com.viewlift.models.data.appcms.subscribeForLatestNewsPojo;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class ResponsePojo{

	@SerializedName("user_exist")
	private UserExist userExist;

	@SerializedName("email_type")
	private String emailType;

	@SerializedName("list_id")
	private String listId;

	@SerializedName("timestamp_opt")
	private String timestampOpt;

	@SerializedName("_links")
	private List<LinksItem> links;

	@SerializedName("merge_fields")
	private MergeFields mergeFields;

	@SerializedName("timestamp_signup")
	private String timestampSignup;

	@SerializedName("ip_signup")
	private String ipSignup;

	@SerializedName("member_rating")
	private int memberRating;

	@SerializedName("language")
	private String language;

	@SerializedName("unique_email_id")
	private String uniqueEmailId;

	@SerializedName("email_address")
	private String emailAddress;

	@SerializedName("email_client")
	private String emailClient;

	@SerializedName("stats")
	private Stats stats;

	@SerializedName("ip_opt")
	private String ipOpt;

	@SerializedName("location")
	private Location location;

	@SerializedName("id")
	private String id;

	@SerializedName("vip")
	private boolean vip;

	@SerializedName("status")
	private String status;

	@SerializedName("last_changed")
	private String lastChanged;

	public UserExist getUserExist() {
		return userExist;
	}

	public void setUserExist(UserExist userExist) {
		this.userExist = userExist;
	}

	public void setEmailType(String emailType){
		this.emailType = emailType;
	}

	public String getEmailType(){
		return emailType;
	}

	public void setListId(String listId){
		this.listId = listId;
	}

	public String getListId(){
		return listId;
	}

	public void setTimestampOpt(String timestampOpt){
		this.timestampOpt = timestampOpt;
	}

	public String getTimestampOpt(){
		return timestampOpt;
	}

	public void setLinks(List<LinksItem> links){
		this.links = links;
	}

	public List<LinksItem> getLinks(){
		return links;
	}

	public void setMergeFields(MergeFields mergeFields){
		this.mergeFields = mergeFields;
	}

	public MergeFields getMergeFields(){
		return mergeFields;
	}

	public void setTimestampSignup(String timestampSignup){
		this.timestampSignup = timestampSignup;
	}

	public String getTimestampSignup(){
		return timestampSignup;
	}

	public void setIpSignup(String ipSignup){
		this.ipSignup = ipSignup;
	}

	public String getIpSignup(){
		return ipSignup;
	}

	public void setMemberRating(int memberRating){
		this.memberRating = memberRating;
	}

	public int getMemberRating(){
		return memberRating;
	}

	public void setLanguage(String language){
		this.language = language;
	}

	public String getLanguage(){
		return language;
	}

	public void setUniqueEmailId(String uniqueEmailId){
		this.uniqueEmailId = uniqueEmailId;
	}

	public String getUniqueEmailId(){
		return uniqueEmailId;
	}

	public void setEmailAddress(String emailAddress){
		this.emailAddress = emailAddress;
	}

	public String getEmailAddress(){
		return emailAddress;
	}

	public void setEmailClient(String emailClient){
		this.emailClient = emailClient;
	}

	public String getEmailClient(){
		return emailClient;
	}

	public void setStats(Stats stats){
		this.stats = stats;
	}

	public Stats getStats(){
		return stats;
	}

	public void setIpOpt(String ipOpt){
		this.ipOpt = ipOpt;
	}

	public String getIpOpt(){
		return ipOpt;
	}

	public void setLocation(Location location){
		this.location = location;
	}

	public Location getLocation(){
		return location;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setVip(boolean vip){
		this.vip = vip;
	}

	public boolean isVip(){
		return vip;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	public void setLastChanged(String lastChanged){
		this.lastChanged = lastChanged;
	}

	public String getLastChanged(){
		return lastChanged;
	}

	@Override
 	public String toString(){
		return 
			"ResponsePojo{" + 
			"email_type = '" + emailType + '\'' + 
			",list_id = '" + listId + '\'' + 
			",timestamp_opt = '" + timestampOpt + '\'' + 
			",_links = '" + links + '\'' + 
			",merge_fields = '" + mergeFields + '\'' + 
			",timestamp_signup = '" + timestampSignup + '\'' + 
			",ip_signup = '" + ipSignup + '\'' + 
			",member_rating = '" + memberRating + '\'' + 
			",language = '" + language + '\'' + 
			",unique_email_id = '" + uniqueEmailId + '\'' + 
			",email_address = '" + emailAddress + '\'' + 
			",email_client = '" + emailClient + '\'' + 
			",stats = '" + stats + '\'' + 
			",ip_opt = '" + ipOpt + '\'' + 
			",location = '" + location + '\'' + 
			",id = '" + id + '\'' + 
			",vip = '" + vip + '\'' + 
			",status = '" + status + '\'' + 
			",last_changed = '" + lastChanged + '\'' + 
			"}";
		}
}