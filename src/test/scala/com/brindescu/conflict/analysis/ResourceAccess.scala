package com.brindescu.conflict.analysis

import java.io.File

trait ResourceAccess {

	def getResourceFile(name: String): String =
		new File(this.getClass.getResource(name).getFile) getAbsolutePath

}
