package com.brindescu.conflict.analysis

import com.ibm.wala.ssa.SSAInstruction
import edu.illinois.wala.Facade._
import edu.illinois.wala.S
import edu.illinois.wala.classLoader.CodeLocation
import edu.illinois.wala.ssa.V

import scala.collection.JavaConversions._

class MethodDU private(private val du: Map[String, List[CodeLocation]],
											 private val m: M) {

	def getMethodSignature: String = {
		m.getSignature
	}

	def getLocalVariables: Set[String] = {
		???
	}

	def getUsesForVariable(v: String): List[CodeLocation] =
		du.getOrElse(v, List())

	override def toString = du.toString
}

object MethodDU {
	private def apply(du: Map[String, List[CodeLocation]], m: M) =
		new MethodDU(du, m)

	def getDUPathsForMethod(method: N): MethodDU = {
		val localUses = method.getIR.iterateAllInstructions.map { i => i.getDef(0) }
			.map(d => d -> getUses(method, d)).toMap
		val paramUses = Range(0, method.getIR.getNumberOfParameters)
			.map(p => method.getIR.getParameter(p))
			.map(d => d -> getUses(method, d)).toMap
		val uses = localUses ++ paramUses
		MethodDU(uses.keys flatMap { value =>
			resolveVariableNames(method, value, uses.get(value).get)
		} map { t => Map(t) } reduce (_ ++ _)
			collect { case (k, v) => k -> v.flatMap { i => resolveInstructionLineNo(method, i) }
		}, method.getMethod)
	}

	private def getUses(method: N, d: Int): List[SSAInstruction] =
		method.getDU.getUses(d).toList

	private def resolveInstructionLineNo(method: N, i: I): Iterable[CodeLocation] =
		S(method, i).codeLocation

	private def resolveVariableNames(method: N, k: Int, uses: List[I]): Set[(String, List[SSAInstruction])] = {
		val names = method.variableNames(V(k))
		if (!names.isEmpty)
			names.map(_ -> uses)
		else
			Set(k.toString -> uses)
	}
}
