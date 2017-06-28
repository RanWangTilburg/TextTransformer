package Util

import scala.reflect._

object Type {
  def newInstance[T: ClassTag](init_args: AnyRef*): T = {
    classTag[T].runtimeClass.getConstructors.head.newInstance(init_args: _*).asInstanceOf[T]
  }
}
