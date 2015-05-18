package com.ishare.bean;

public class UserBean {

	public UserBean() {
	}

	public UserBean(String username, String gender, String nickname, String job) {
		super();
		this.username = username;
		this.gender = gender;
		this.nickname = nickname;
		this.job = job;
	}

	// nullable, useful
	private long id;

	private String username;

	private String password;

	// nullable
	private int age;

	private String gender;

	private String nickname;

	private String role;

	private String job;

	// nullable
	private String charactor;

	// nullable
	private IdentityBean identityBean;

	private CarBean carBean;

	// nullable
	private PaymentBean paymentBean;

	// nullable, but useful
	private String token;

	// nullable is fine, useful, will update head pic automatically refer to
	// gender and job
	private String headPic;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public IdentityBean getIdentityBean() {
		return identityBean;
	}

	public void setIdentityBean(IdentityBean identityBean) {
		this.identityBean = identityBean;
	}

	public CarBean getCarBean() {
		return carBean;
	}

	public void setCarBean(CarBean carBean) {
		this.carBean = carBean;
	}

	public PaymentBean getPaymentBean() {
		return paymentBean;
	}

	public void setPaymentBean(PaymentBean paymentBean) {
		this.paymentBean = paymentBean;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getCharactor() {
		return charactor;
	}

	public void setCharactor(String charactor) {
		this.charactor = charactor;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getHeadPic() {
		return headPic;
	}

	public void setHeadPic(String headPic) {
		this.headPic = headPic;
	}

	@Override
	public String toString() {
		return "UserBean [id=" + id + ", username=" + username + ", password="
				+ password + ", age=" + age + ", gender=" + gender
				+ ", nickname=" + nickname + ", role=" + role + ", job=" + job
				+ ", charactor=" + charactor + ", identityBean=" + identityBean
				+ ", carBean=" + carBean + ", paymentBean=" + paymentBean
				+ ", token=" + token + ", headPic=" + headPic + "]";
	}

}
