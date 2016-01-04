package tests;

import org.junit.Assert;

import solution.Given;
import solution.Then;
import solution.When;

import java.util.Date;

public class DogStoryTest implements Cloneable{
	protected Dog dog;
	protected String str;
	//protected java.util.Date date;
	public DogStoryTest(){
		str = "Initial value";
		//date = new Date();
		//date.setMonth(1);
	}
	@Given("a Dog of age &age")
	public void aDog(Integer age) {
		dog = new Dog(age);
		dog.stupid_dog=false;
	}
	
	@When("the dog is not taken out for a walk, and the number of hours is &hours")
	public void dogNotTakenForAWalk(Integer hours) {
		if (hours==10)
			dog.stupid_dog=true;
		dog.notTakenForAWalk(hours);
	}
	
	@Then("the house condition is &condition")
	public void theHouseCondition(String condition) {
		if (dog.stupid_dog || (!condition.equals(dog.houseCondition()))){
			str = "then going to fail";
			//date.setMonth(2);
		}
		if (dog.stupid_dog)
			Assert.assertEquals("1","2");
		Assert.assertEquals(condition, dog.houseCondition());
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException{
		DogStoryTest newObject = (DogStoryTest) super.clone();
		newObject.dog =  new Dog(this.dog.age);
		return newObject;
	}
}
