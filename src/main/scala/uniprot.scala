package com.bio4j.data.uniprot

import com.bio4j.data._
import com.bio4j.model._
import com.bio4j.angulillos._
import scala.xml._

case class Entry(val xml: Elem) extends AnyVal
case object Entry {

  implicit def asXML: Entry => Elem = _.xml
}

case class UniProtImport[V,E](val graph: UniProtGraph[V,E]) {

  lazy val importProteins = AddVertex.generically[V,E](
    graph,
    graph.protein,
    {
      (entry: Entry, g: UniProtGraph[V,E]) =>
        Seq (
          g.protein.addVertex
          .set( g.protein.accession,    entry \ "accession" text  )
          .set( g.protein.sequence,     entry \ "sequence" text   )
          .set( g.protein.isCanonical,  true: java.lang.Boolean   )
          .set(
            g.protein.dataset,
            (entry \ "@dataset" text) match {
              case "Swiss-Prot" => UniProtGraph.Datasets.swissProt
              case _            => UniProtGraph.Datasets.trEMBL
            }
          )
        // .set( g.protein.existence,    (entry \ "proteinExistence" \ "@type" text) )
        )
    }
  )
}
