package com.brindescu.conflict.detector

import org.scalatest.{FlatSpec, Matchers}

class ConflictDetectorTest extends FlatSpec with Matchers {

	it should "detect a conflict" in {
		ConflictDetector.findConflictBetween("public class A1{}", "public class A2{}", "public class A{}") should be (true)
	}

	it should "not detect a conflict" in {
		ConflictDetector.findConflictBetween("public class A1{}", "private class A{}", "public class A{}") should be (false)
	}
}
