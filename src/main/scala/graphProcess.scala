package com.bio4j.release.generic

import com.bio4j.angulillos._
import com.bio4j.model._

trait AnyGraphProcess {

  /* The types representing vertices and edges. Everything should be generic on these two types. */
  type V
  type E

  /*
    The input type used for importing the vertex; a csv row, an xml element, ...
  */
  type Input
  type Output

  /*
    The graph which we are working with.
  */
  type Graph <: TypedGraph[Graph,V,E]

  /*
    The actual process. You have as input `From` and the `Graph`, and you should return the added vertices.
  */
  def process: (Input,Graph) => (Graph,Output)
}

case class GraphProcess[V0, E0, Graph0 <: TypedGraph[Graph0,V0,E0], Input0, Output0](
  val graph: Graph0,
  val process: (Input0,Graph0) => (Graph0,Output0)
)
extends AnyGraphProcess {

  type V      = V0
  type E      = E0
  type Input  = Input0
  type Output = Output0
  type Graph  = Graph0
}
//
case object GraphProcess {

  /*
    This method (and class) is used for leaving V,E free while facilitating type inference of the rest of type parameters.
  */
  def generically[V0,E0]: generically[V0,E0] = new generically[V0,E0]

  class generically[V0,E0] {

    def apply[
      Graph0 <: TypedGraph[Graph0,V0,E0],
      Input0,
      Output0
    ](
      graph   : Graph0,
      process : (Input0,Graph0) => (Graph0,Output0)
    )
    : GraphProcess[V0,E0,Graph0,Input0,Output0] =
      GraphProcess(graph,process)
  }
}
