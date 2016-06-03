package com.brindescu.conflict.analysis

import org.scalatest.{FlatSpec, Matchers}

class MethodDUTest extends FlatSpec with Matchers with ResourceAccess {

	private def getOverloadedAnalysis: Analysis =
		new Analysis()
			.setEntryPoint(".*OverloadedDefUse.*m.*")
			.addSourceDependency(getResourceFile("/def-use"))
			.setExclusion("")

	it should "generate a correct DU graph" in {
		val analysis = new Analysis()
			.setEntryPoint(".*DefUseTest.*m.*")
			.addSourceDependency(getResourceFile("/def-use"))
		  .setExclusion("")

		val withVariables = analysis.getDUPathsForMethod("m")

		withVariables should have size 1
		withVariables.head.getUsesForVariable("x") should have size 2
		withVariables.head.getUsesForVariable("y") should have size 1
		withVariables.head.getUsesForVariable("z") should have size 1
	}

	it should "generate multiple DU graphs for an overloaded method" in {
		val analysis = getOverloadedAnalysis

		val withVariables = analysis.getDUPathsForMethod("m")
		withVariables should have size 2
		withVariables.head.getUsesForVariable("x") should have size 1
		withVariables.last.getUsesForVariable("y") should have size 1
	}

	it should "generate uses for parameters" in {
		val analysis = getOverloadedAnalysis

		val withVariables = analysis.getDUPathsForMethod("m")
		withVariables.last.getUsesForVariable("a") should have size 1
	}

	it should "have the correct signature" in {
		val analysis = getOverloadedAnalysis

		val withVariables = analysis.getDUPathsForMethod("m")
		withVariables.head.getMethodSignature should equal ("OverloadedDefUse.m()V")
	}
}
