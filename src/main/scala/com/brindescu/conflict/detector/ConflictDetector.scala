package com.brindescu.conflict.detector

import com.brindescu.gumtree.facade.ASTDiff
import org.eclipse.jdt.core.dom.{ASTMatcher, ASTNode}
import com.brindescu.gumtree.facade.Gumtree._

object ConflictDetector {

	private class EqualityWrapper(private val n: ASTNode) {

		override def equals(that: Any): Boolean =
			that match {
				case that: EqualityWrapper => that.n.subtreeMatch(new ASTMatcher(), n)
				case _ => false
			}

		override def hashCode(): Int =
			n.getNodeType + n.getLength + n.getStartPosition + 29
	}

	def findConflictBetween(one: String, two: String, base: String): Boolean = {
		val firstChanges = ASTDiff.getDiff(one, base)
		val secondChanges = ASTDiff.getDiff(two, base)

		val nodesTouchedByFirstChange = firstChanges.getActions
			.map(a => firstChanges.getMatch(a.getNode))
			.filter(n => n.isDefined)
		  .map(n => n.get)
		  .map(n => new EqualityWrapper(n.getNode))

		val nodesTouchedBySecondChange = secondChanges.getActions
			.map(a => secondChanges.getMatch(a.getNode))
			.filter(n => n.isDefined)
		  .map(n => n.get)
			.map(n => new EqualityWrapper(n.getNode))

		val conflicts = nodesTouchedByFirstChange.intersect(nodesTouchedBySecondChange)

		return conflicts.nonEmpty
	}
}
