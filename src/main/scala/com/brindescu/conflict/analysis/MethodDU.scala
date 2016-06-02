package com.brindescu.conflict.analysis

import edu.illinois.wala.classLoader.CodeLocation

class MethodDU(private val du: Map[Set[String], List[Option[CodeLocation]]]) {

	def getMethodSignature: String = {
		???
	}

	def getLocalVariables: Set[String] = {
		???
	}

	def getUsesForVariable(v: String): List[CodeLocation] =
		du.get(Set(v)).get collect { case Some(x) => x }
}

object MethodDU {
	def apply(du: Map[Set[String], List[Option[CodeLocation]]]) =
		new MethodDU(du)
}
