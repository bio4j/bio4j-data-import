package com.bio4j.data

import com.bio4j.angulillos._
import com.bio4j.model._

/*
  This trait groups the import process for a vertex.
*/
trait AnyAddVertex {

  /* The types representing vertices and edges. Everything should be generic on these two types. */
  type V
  type E

  /*
    The input type used for importing the vertex; a csv row, an xml element, ...
  */
  type From

  /*
    The graph and vertex, vertex type tuple which we are working with.
  */
  type Graph      <: TypedGraph[Graph,V,E]
  type Vertex     <: TypedGraph[Graph,V,E]#Vertex[Vertex]
  type VertexType <: TypedGraph[Graph,V,E]#VertexType[Vertex]

  /*
    The actual "add vertex" (or vertices) process. You have as input `From` and the `Graph`, and you should return the added vertices.
  */
  def addVertex: (From,Graph) => Seq[Vertex]
}

case class AddVertex[
  V0,
  E0,
  From0,
  Graph0      <: TypedGraph[Graph0,V0,E0],
  Vertex0     <: TypedGraph[Graph0,V0,E0]#Vertex[Vertex0],
  VertexType0 <: TypedGraph[Graph0,V0,E0]#VertexType[Vertex0]
](
  val graph: Graph0,
  val vertexType: VertexType0,
  val addVertex: (From0,Graph0) => Seq[Vertex0]
)
extends AnyAddVertex {

  type V          = V0
  type E          = E0
  type From       = From0
  type Graph      = Graph0
  type Vertex     = Vertex0
  type VertexType = VertexType0
}

case object AddVertex {

  /*
    This method (and class) is used for leaving V,E free while facilitating type inference of the rest of type parameters.
  */
  def generically[V0,E0]: generically[V0,E0] = new generically[V0,E0]
  class generically[V0,E0] {

    def apply[
      From0,
      Graph0      <: TypedGraph[Graph0,V0,E0],
      Vertex0     <: TypedGraph[Graph0,V0,E0]#Vertex[Vertex0],
      VertexType0 <: TypedGraph[Graph0,V0,E0]#VertexType[Vertex0]
    ](
      graph: Graph0,
      vertexType: VertexType0,
      addVertex: (From0,Graph0) => Seq[Vertex0]
    )
    : AddVertex[V0,E0,From0,Graph0,Vertex0,VertexType0] =
      AddVertex(graph,vertexType,addVertex)
  }
}
