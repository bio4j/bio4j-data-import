package com.bio4j.data.uniref

import com.bio4j.data._
import com.bio4j.model._
import com.bio4j.angulillos._
import scala.xml._
import scala.compat.java8.OptionConverters._

case class ImportProcess[V,E](val graph: UniRefGraph[V,E]) {

  type G = UniRefGraph[V,E]

  val uniref50 =
    GraphProcess.generically[V,E] (
      graph,
      (entry: Entry, g: G) =>
        (
          graph,
          {
            val cluster =
              g.uniRef50Cluster.addVertex
                .set(g.uniRef50Cluster.id, entry.ID)

            val representative =
              proteinByAccession(entry.representativeID)

            val seed =
              proteinByAccession(entry.seedID)

            val members =
              (representative +: entry.nonRepresentativeMemberIDs.map(proteinByAccession)).flatten

            representative.foreach {
              rep => g.uniRef50Representative.addEdge(cluster, rep)
            }

            seed.foreach {
              sd => g.uniRef50Seed.addEdge(cluster, sd)
            }

            members.foreach {
              member => g.uniRef50Member.addEdge(cluster, member)
            }
          }
        )
    )

  val uniref90 =
    GraphProcess.generically[V,E] (
      graph,
      (entry: Entry, g: G) =>
        (
          graph,
          {
            val cluster =
              g.uniRef90Cluster.addVertex
                .set(g.uniRef90Cluster.id, entry.ID)

            val representative =
              proteinByAccession(entry.representativeID)

            val seed =
              proteinByAccession(entry.seedID)

            val members =
              (representative +: entry.nonRepresentativeMemberIDs.map(proteinByAccession)).flatten

            representative.foreach {
              rep => g.uniRef90Representative.addEdge(cluster, rep)
            }

            seed.foreach {
              sd => g.uniRef90Seed.addEdge(cluster, sd)
            }

            members.foreach {
              member => g.uniRef90Member.addEdge(cluster, member)
            }
          }
        )
    )

  val uniref100 =
    GraphProcess.generically[V,E] (
      graph,
      (entry: Entry, g: G) =>
        (
          graph,
          {
            val cluster =
              g.uniRef100Cluster.addVertex
                .set(g.uniRef100Cluster.id, entry.ID)

            val representative =
              proteinByAccession(entry.representativeID)

            val seed =
              proteinByAccession(entry.seedID)

            val members =
              (representative +: entry.nonRepresentativeMemberIDs.map(proteinByAccession)).flatten

            representative.foreach {
              rep => g.uniRef100Representative.addEdge(cluster, rep)
            }

            seed.foreach {
              sd => g.uniRef100Seed.addEdge(cluster, sd)
            }

            members.foreach {
              member => g.uniRef100Member.addEdge(cluster, member)
            }
          }
        )
    )

  private def proteinByAccession(accession: String) =
    graph.uniProtGraph.protein.accession.index.find(accession).asScala
}
