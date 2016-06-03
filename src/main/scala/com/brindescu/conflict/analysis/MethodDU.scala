package com.brindescu.conflict.analysis

import edu.illinois.wala.classLoader.CodeLocation

class MethodDU(private val du: Map[String, List[CodeLocation]]) {

	def getMethodSignature: String = {
		???
	}

	def getLocalVariables: Set[String] = {
		???
	}

	def getUsesForVariable(v: String): List[CodeLocation] =
		du.getOrElse(v, List())

	override def toString = du.toString
}

object MethodDU {
	def apply(du: Map[String, List[CodeLocation]]) =
		new MethodDU(du)
}
