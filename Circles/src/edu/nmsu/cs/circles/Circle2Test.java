package edu.nmsu.cs.circles;

/***
 * Example JUnit testing class for Circle2 (and Circle)
 *
 * - must have your classpath set to include the JUnit jarfiles - to run the test do: java
 * org.junit.runner.JUnitCore Circle2Test - note that the commented out main is another way to run
 * tests - note that normally you would not have print statements in a JUnit testing class; they are
 * here just so you see what is happening. You should not have them in your test cases.
 ***/

import org.junit.*;

public class Circle2Test
{
	// Data you need for each test case
	private Circle2 circle2;

	//
	// Stuff you want to do before each test case
	//
	@Before
	public void setup()
	{
		System.out.println("\nTest starting...");
		circle2 = new Circle2(1, 2, 3);
	}

	//
	// Stuff you want to do after each test case
	//
	@After
	public void teardown()
	{
		System.out.println("\nTest finished.");
	}

	//
	// Test circle initialization with all positive values
	//
	@Test
	public void circleInitializationTestPositive()
	{
		Circle2 testCircle;
		System.out.println("Running test circleInitializationTestPositive.");
		testCircle = new Circle2(1, 3, 7);
		Assert.assertTrue(testCircle.center.x == 1 &&
						  testCircle.center.y == 3 &&
						  testCircle.radius == 7);
	}

	//
	// Test circle initialization with all negative values
	//
	@Test
	public void circleInitializationTestNegative()
	{
		Circle2 testCircle;
		System.out.println("Running test circleInitializationTestNegative.");
		testCircle = new Circle2(-1, -3, -7);
		Assert.assertTrue(testCircle.center.x == -1 &&
						  testCircle.center.y == -3 &&
						  testCircle.radius == 7);
	}

	//
	// Test a simple positive move
	//
	@Test
	public void simpleMove()
	{
		Point p;
		System.out.println("Running test simpleMove.");
		p = circle2.moveBy(1, 1);
		Assert.assertTrue(p.x == 2 && p.y == 3);
	}

	//
	// Test a simple negative move
	//
	@Test
	public void simpleMoveNeg()
	{
		Point p;
		System.out.println("Running test simpleMoveNeg.");
		p = circle2.moveBy(-1, -1);
		Assert.assertTrue(p.x == 0 && p.y == 1);
	}

	//
	// Test radius increase
	//
	@Test
	public void radiusIncrease()
	{
		double r;
		System.out.println("Running test radiusIncrease.");
		r = circle2.scale(2);
		Assert.assertTrue(r == 6);
	}

	//
	// Test radius decrease
	//
	@Test
	public void radiusDecrease()
	{
		double r;
		System.out.println("Running test radiusDecrease.");
		r = circle2.scale(0.5);
		Assert.assertTrue(r == 1.5);
	}

	//
	// Test intersection of two circles with changing x values;
	// returns true
	//
	@Test
	public void intersectOnlyXChange()
	{
		System.out.println("Running test intersectOnlyXChange.");
		Circle2 testCircle;
		testCircle = new Circle2(8, 2, 5);
		Assert.assertTrue(circle2.intersects(testCircle));
	}

	//
	// Test intersection of two circles with changing y values;
	// returns true
	//
	@Test
	public void intersectOnlyYChange()
	{
		System.out.println("Running test intersectOnlyYChange.");
		Circle2 testCircle;
		testCircle = new Circle2(8, 2, 5);
		Assert.assertTrue(circle2.intersects(testCircle));
	}

	//
	// Test intersection of two circles with changing x and y values;
	// returns true
	//
	@Test
	public void intersectXYChange()
	{
		System.out.println("Running test intersectOnlyYChange().");
		Circle2 testCircle;
		testCircle = new Circle2(7, 6, 5);
		Assert.assertTrue(circle2.intersects(testCircle));
	}

	//
	// Test no intersection of two circles; returns false
	//
	@Test
	public void noIntersection()
	{
		System.out.println("Running test noIntersection.");
		Circle2 testCircle;
		testCircle = new Circle2(9, 7, 5);
		Assert.assertTrue(!circle2.intersects(testCircle));
	}

	

	/***
	 * NOT USED public static void main(String args[]) { try { org.junit.runner.JUnitCore.runClasses(
	 * java.lang.Class.forName("Circle2Test")); } catch (Exception e) { System.out.println("Exception:
	 * " + e); } }
	 ***/

}
