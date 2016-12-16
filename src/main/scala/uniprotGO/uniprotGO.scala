// package com.bio4j.data.uniprotGO
//
// import com.bio4j.data._
// import com.bio4j.model._
// import com.bio4j.angulillos._
// import scala.compat.java8.OptionConverters._
// import uniprot.Entry
//
// case class Process[V,E](val graph: UniProtGOGraph[V,E]) {
//
//   type G = UniProtGOGraph[V,E]
//
//   val goAnnotations =
//     GraphProcess.generically[V,E] (
//       graph,
//       (entry: Entry, g: G) =>
//         (
//           graph,
//           {
//
//             val goTerms =
//               entry.dbReferenceIDs(tpe = "GO")
//                 .map( id => g.goGraph.term.id.index.find(id).asScala )
//                 .flatten
//
//             val canonicalProtein =
//               g.uniProtGraph.protein.accession.index.find(entry.accession).asScala
//
//             goTerms.map( term =>
//                 canonicalProtein.map { p =>
//                   g.annotation.addEdge(p, term)
//                 }
//               )
//               .flatten
//           }
//         )
//     )
// }
