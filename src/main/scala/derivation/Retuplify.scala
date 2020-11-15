package derivation

import scala.quoted._
//
import scala.compiletime.{erasedValue, summonFrom}
//import dotty.tools.dotc.ast.untpd
//import dotty.tools.dotc.core.Names._

object Retuplify {
  inline def retuplify[T](tuple: Tuple, newValue: T): Tuple = ${ retuplifyImpl('tuple, 'newValue) }
  def retuplifyImpl[T:Type](tuple: Expr[Tuple], newValue: Expr[T])(using qctx: QuoteContext): Expr[Tuple] = {
    import qctx.reflect.{_}
  
    printer.ln(tuple.unseal.underlyingArgument)

    // tuple match {
    //   case '{ ($elem *: ($elems1: $tpe)) } => println(s"ELEM: $elem ELEMS: $elems1")
    // }

    '{null}
  }
}

object ChangeFoosToBars {
  inline def changeFoos(block: =>String): String = ${ changeFoosImpl('block) }
  def changeFoosImpl(block: Expr[String])(using qctx: QuoteContext): Expr[String] = {
    import qctx.reflect.{_}
    //printer.ln(block.unseal)

    object Unseal {
      def unapply(t: Expr[Any])(using qctx: QuoteContext) = {
        Some(t.unseal)
      }
    }
    object Seal {
      def unapply[T](e: Term)(using qctx: QuoteContext) = {
        implicit val ttpe: quoted.Type[T] = e.tpe.seal.asInstanceOf[quoted.Type[T]]
        Some(e.seal.cast[T])
      }
    }

    object rewriter extends util.ExprMap {
      def transform[T](e: Expr[T])(using QuoteContext, quoted.Type[T]): Expr[T] = e match {
        //case '{ (${Unseal(Ident("foo"))}: String) } => 
        //  '{ "blahblah" }.cast[T]
        case _ => transformChildren(e)
      }
    }

    rewriter.transform[String](block)
  }
}


