package com.ishare.bean;

public class PoolSubjectBean {

	// nullable, useful
	private long id;

	private String gender;

	// nullable
	private String atmosphere;

	private String job;

	// nullable
	private String age;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAtmosphere() {
		return atmosphere;
	}

	public void setAtmosphere(String atmosphere) {
		this.atmosphere = atmosphere;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "PoolSubjectBean [id=" + id + ", gender=" + gender
				+ ", atmosphere=" + atmosphere + ", job=" + job + ", age="
				+ age + "]";
	}
}
