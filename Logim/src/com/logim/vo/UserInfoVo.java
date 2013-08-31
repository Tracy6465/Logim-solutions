package com.logim.vo;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInfoVo implements Parcelable{
	String useremail;
	String userid; 
	String appid;
	String type; 
	String status; 
	String identitiesstoragelimit;
	String signaturekey;
	String refemail;
	String reftwitter;
	String reffacebook;
	String refweibo;
	String premexp;
	
	public UserInfoVo() {
		super();
	}

	public String getUseremail() {
		return useremail;
	}

	public void setUseremail(String useremail) {
		this.useremail = useremail;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIdentitiesstoragelimit() {
		return identitiesstoragelimit;
	}

	public void setIdentitiesstoragelimit(String identitiesstoragelimit) {
		this.identitiesstoragelimit = identitiesstoragelimit;
	}

	public String getSignaturekey() {
		return signaturekey;
	}

	public void setSignaturekey(String signaturekey) {
		this.signaturekey = signaturekey;
	}

	public String getRefemail() {
		return refemail;
	}

	public void setRefemail(String refemail) {
		this.refemail = refemail;
	}

	public String getReftwitter() {
		return reftwitter;
	}

	public void setReftwitter(String reftwitter) {
		this.reftwitter = reftwitter;
	}

	public String getReffacebook() {
		return reffacebook;
	}

	public void setReffacebook(String reffacebook) {
		this.reffacebook = reffacebook;
	}

	public String getRefweibo() {
		return refweibo;
	}

	public void setRefweibo(String refweibo) {
		this.refweibo = refweibo;
	}

	public String getPremexp() {
		return premexp;
	}

	public void setPremexp(String premexp) {
		this.premexp = premexp;
	}
	
	public static Creator<UserInfoVo> CREATOR = 
			new Creator<UserInfoVo>() {
				
				@Override
				public UserInfoVo[] newArray(int size) {
					return new UserInfoVo[size];
				}
				
				@Override
				public UserInfoVo createFromParcel(Parcel source) {
					UserInfoVo userInfo = new UserInfoVo();
					userInfo.setUseremail(source.readString());
					userInfo.setUserid(source.readString());
					userInfo.setAppid(source.readString());
					userInfo.setType(source.readString());
					userInfo.setStatus(source.readString());
					userInfo.setIdentitiesstoragelimit(source.readString());
					userInfo.setSignaturekey(source.readString());
					userInfo.setRefemail(source.readString());
					userInfo.setReffacebook(source.readString());
					userInfo.setReftwitter(source.readString());
					userInfo.setRefweibo(source.readString());
					userInfo.setPremexp(source.readString());
					
					return userInfo;
				}
			};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(useremail);
		dest.writeString(userid);
		dest.writeString(appid);
		dest.writeString(type);
		dest.writeString(status);
		dest.writeString(identitiesstoragelimit);
		dest.writeString(signaturekey);
		dest.writeString(refemail);
		dest.writeString(reftwitter);
		dest.writeString(reffacebook);
		dest.writeString(refweibo);
		dest.writeString(premexp);
	}
	
}
