package cn.fxlcy.library.exemple;

import cn.fxlcy.library.OnValueChangeListener;
import cn.fxlcy.library.ValueChangeListenerProxy;

public class Test {
	public static void main(String[] args){
		User u = ValueChangeListenerProxy.newInstance(User.class,new OnValueChangeListener() {
			
			@Override
			public void onChanged(String fieldName, Object oldValue, Object newValue) {
				System.out.println(fieldName);
				System.out.println(oldValue);
				System.out.println(newValue);
			}
		});
		
		u.setUserName("fxlcy");
	}
	
	
	public static class User{
		private String userName;
		private String password;
		private String phoneName;
		
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getPhoneName() {
			return phoneName;
		}
		public void setPhoneName(String phoneName) {
			this.phoneName = phoneName;
		}
	}
}
