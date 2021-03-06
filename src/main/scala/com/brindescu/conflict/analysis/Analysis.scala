package com.brindescu.conflict.analysis

import com.brindescu.conflict.analysis.WALAConstants._
import com.ibm.wala.ipa.callgraph.impl.ContextInsensitiveSelector
import com.ibm.wala.ipa.callgraph.propagation.cfa.nCFAContextSelector
import com.ibm.wala.ssa.{SSAAbstractInvokeInstruction, SSAInstruction}
import com.typesafe.config.{Config, ConfigFactory, ConfigList, ConfigValueFactory}
import edu.illinois.wala.Facade._
import edu.illinois.wala.S
import edu.illinois.wala.classLoader.CodeLocation
import edu.illinois.wala.ipa.callgraph.FlexibleCallGraphBuilder
import edu.illinois.wala.ssa.V

import scala.collection.JavaConversions._

class Analysis {

	private var config: Config = ConfigFactory.empty()
	var ncfa = 0

	lazy val pa = doPointerAnalysis

	addJavaLibraries

	def addJavaLibraries = {
		val clazz = new Object().getClass
		val classFile = clazz.getResource(clazz.getSimpleName + ".class").getFile
		val rtPath: String = if (classFile.contains(".jar!"))
			classFile.split("!")(0).split(":")(1)
		else
			classFile
		config = config.withValue(JRE_LIBRARY, ConfigValueFactory.fromAnyRef(rtPath))
	}

	private def getConfigList(folder: String): ConfigList =
		getConfigList(List(folder))

	private def getConfigList(list: List[String]): ConfigList =
		ConfigValueFactory.fromIterable(list)

	private def getExisting(key: String): List[String] =
		if (config.hasPath(key))
			config.getValue(key).unwrapped().asInstanceOf[List[String]]
		else
			List()

	def addSourceDependency(folder: String): Analysis = {
		config = config.withValue(SOURCE_DEPENDENCY, getConfigList(getExisting(SOURCE_DEPENDENCY) :+ folder))
		this
	}

	def addJarDependency(jarPath: String): Analysis = {
		config = config.withValue(JAR_DEPENDENCY, getConfigList(getExisting(JAR_DEPENDENCY) :+ jarPath))
		this
	}

	def addBinaryDepedency(classFolderPath: String): Analysis = {
		config = config.withValue(BINARY_DEPENDENCY, getConfigList(getExisting(BINARY_DEPENDENCY) :+ classFolderPath))
		this
	}

	def setEntryPoint(pattern: String): Analysis = {
		config = config.withValue(ENTRY_POINT, ConfigValueFactory.fromAnyRef(pattern))
		this
	}

	def setExclusion(pattern: String): Analysis = {
		config = config.withValue(EXCLUSIONS, ConfigValueFactory.fromAnyRef(pattern))
		this
	}

	def setNCFA(ncfa: Int): Analysis = {
		this.ncfa = ncfa
		this
	}

	def makeQuiet(): Analysis = {
		config = config.withValue(QUIET, ConfigValueFactory.fromAnyRef("true"))
		this
	}

	def doPointerAnalysis() = {
		implicit val config = this.config
		new FlexibleCallGraphBuilder() {
			override def cs = new nCFAContextSelector(ncfa, new ContextInsensitiveSelector())
		}
	}


	def getPointerAnalysis() = pa

	def getDUPathsForMethod(methodName: String): List[MethodDU] = {
		val cg = pa.cg
		val methods = cg filter {_.m.name == methodName	}
		methods map { MethodDU.getDUPathsForMethod(_) } toList
	}
}

private object WALAConstants {
	val JRE_LIBRARY = "wala.jre-lib-path"
	val SOURCE_DEPENDENCY = "wala.dependencies.source"
	val BINARY_DEPENDENCY = "wala.dependencies.binary"
	val JAR_DEPENDENCY = "wala.dependencies.jar"
	val ENTRY_POINT = "wala.entry.signature-pattern"
	val EXCLUSIONS = "wala.exclusions"
	val QUIET = "wala.config.quiet"
}
