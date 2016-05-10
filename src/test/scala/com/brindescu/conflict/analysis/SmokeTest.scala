package com.brindescu.conflict.analysis

import java.io.File

import com.ibm.wala.ipa.callgraph.impl.ContextInsensitiveSelector
import com.ibm.wala.ipa.callgraph.propagation.cfa.nCFAContextSelector
import com.ibm.wala.util.graph.traverse.DFS
import com.typesafe.config.{Config, ConfigFactory, ConfigValueFactory}
import edu.illinois.wala.Facade._
import edu.illinois.wala.ipa.callgraph.FlexibleCallGraphBuilder
import edu.illinois.wala.ipa.callgraph.propagation.P
import org.scalatest.{FlatSpec, Matchers}
import scala.collection.JavaConversions._

class SmokeTest extends FlatSpec with Matchers {

	it should "correctly run WALA" in {

		def createConfig(): Config = {
			val clazz = new Object().getClass
			val rtPath = clazz.getResource(clazz.getSimpleName + ".class").getFile.split("!")(0).split(":")(1)
			ConfigFactory.empty().withValue("wala.jre-lib-path", ConfigValueFactory.fromAnyRef(rtPath))
				.withValue("wala.entry.signature-pattern", ConfigValueFactory.fromAnyRef(".*Foo.*main.*"))
				//		  .withValue("wala.dependencies-binary", ConfigValueFactory.fromAnyRef("target/scala-2.11/classes"))
				.withValue("wala.dependencies.source", ConfigValueFactory.fromIterable(List(getResourceFile("/wala-smoke"))))
				.withValue("wala.exclusions", ConfigValueFactory.fromAnyRef(""))
		}

		def getResourceFile(name: String): String =
			new File(this.getClass.getResource(name).getFile).getAbsolutePath

		implicit val config = createConfig()

		val pa = new FlexibleCallGraphBuilder() {
			override def cs = new nCFAContextSelector(0, new ContextInsensitiveSelector())
		}

		import pa._

		val nodes = DFS.getReachableNodes(cg, cg filter { _.m.name == "bar"})
			.flatMap { n => n.instructions collect { case i: PutI => (i.f.get, P(n, i.v).variableNames()) } }
		print(nodes)

		nodes should have size 3
	}

}
