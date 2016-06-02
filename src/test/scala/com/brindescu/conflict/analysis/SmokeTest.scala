package com.brindescu.conflict.analysis

import java.io.File

import com.ibm.wala.util.graph.traverse.DFS
import edu.illinois.wala.Facade._
import edu.illinois.wala.classLoader.CodeLocation
import edu.illinois.wala.ipa.callgraph.propagation.P
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.JavaConversions._

class SmokeTest extends FlatSpec with Matchers {

	def getResourceFile(name: String): String =
		new File(this.getClass.getResource(name).getFile).getAbsolutePath

	it should "correctly run WALA" in {


		val analysis = new Analysis()
		  .setEntryPoint(".*Foo.*main.*")
		  .addSourceDependency(getResourceFile("/wala-smoke"))
		  .setExclusion("")


		val pa = analysis.getPointerAnalysis

		import pa._

		val nodes = DFS.getReachableNodes(cg, cg filter { _.m.name == "bar"})
			.flatMap { n => n.instructions collect { case i: PutI => (i.f.get, P(n, i.v).variableNames()) } }

		nodes should have size 3
	}

	it should "get a def-use graph" in {
		val analysis = new Analysis()
		  .setEntryPoint(".*DefUseTest.*m.*")
		  .addSourceDependency(getResourceFile("/def-use"))
		  .setExclusion("")

		val pa = analysis.getPointerAnalysis()

		val withVariables = analysis.getDUPathsForMethod("m")
		println(withVariables)
	}
}
