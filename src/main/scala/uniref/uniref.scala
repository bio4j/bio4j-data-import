package com.bio4j.release.generic.uniref

import com.bio4j.data.uniref._
import com.bio4j.model._
import scala.compat.java8.OptionConverters._

case class ImportUniRef[V,E](val graph: UniRefGraph[V,E]) {

  type G = UniRefGraph[V,E]
  def g: G = graph

  def uniref50Cluster(clusterEntry: AnyCluster): Option[G#UniRef50Cluster] = {
    clusterEntry.uniProtRepresentative flatMap { rep =>
      proteinByAccession(rep.id) map { p =>

        val cluster =
          g.uniRef50Cluster.addVertex
            .set(g.uniRef50Cluster.id, clusterEntry.ID)

        val repEdge =
          g.uniRef50Representative.addEdge(cluster, p)

        val members =
          clusterEntry.uniProtNonRepresentativeMembers
            .collect(Function.unlift(up => proteinByAccession(up.id)))

        val membersEdges =
          members map { g.uniRef50Member.addEdge(cluster, _) }

        val seedEdge =
          clusterEntry.uniProtSeed.flatMap(up => proteinByAccession(up.id)) map {
            g.uniRef50Seed.addEdge(cluster, _)
          }

        cluster
      }
    }
  }

  def uniref90Cluster(clusterEntry: AnyCluster): Option[G#UniRef90Cluster] = {
    clusterEntry.uniProtRepresentative flatMap { rep =>
      proteinByAccession(rep.id) map { p =>

        val cluster =
          g.uniRef90Cluster.addVertex
            .set(g.uniRef90Cluster.id, clusterEntry.ID)

        val repEdge =
          g.uniRef90Representative.addEdge(cluster, p)

        val members =
          clusterEntry.uniProtNonRepresentativeMembers
            .collect(Function.unlift(up => proteinByAccession(up.id)))

        val membersEdges =
          members map { g.uniRef90Member.addEdge(cluster, _) }

        val seedEdge =
          clusterEntry.uniProtSeed.flatMap(up => proteinByAccession(up.id)) map {
            g.uniRef90Seed.addEdge(cluster, _)
          }

        cluster
      }
    }
  }

  def uniref100Cluster(clusterEntry: AnyCluster): Option[G#UniRef100Cluster] = {
    clusterEntry.uniProtRepresentative flatMap { rep =>
      proteinByAccession(rep.id) map { p =>

        val cluster =
          g.uniRef100Cluster.addVertex
            .set(g.uniRef100Cluster.id, clusterEntry.ID)

        val repEdge =
          g.uniRef100Representative.addEdge(cluster, p)

        val members =
          clusterEntry.uniProtNonRepresentativeMembers
            .collect(Function.unlift(up => proteinByAccession(up.id)))

        val membersEdges =
          members map { g.uniRef100Member.addEdge(cluster, _) }

        val seedEdge =
          clusterEntry.uniProtSeed.flatMap(up => proteinByAccession(up.id)) map {
            g.uniRef100Seed.addEdge(cluster, _)
          }

        cluster
      }
    }
  }

  private def proteinByAccession(accession: String) =
    graph.uniProtGraph.protein.accession.index.find(accession).asScala

  implicit class ClusterOps(val cluster: AnyCluster) {

    def uniProtRepresentative: Option[UniProtProtein] =
      uniProtProteinMember( cluster.representative )

    def uniProtSeed: Option[UniProtProtein] =
      uniProtProteinMember( cluster.seed )

    def uniProtNonRepresentativeMembers: Seq[UniProtProtein] =
      cluster.nonRepresentativeMembers collect Function.unlift(uniProtProteinMember)

    private def uniProtProteinMember(cm: ClusterMember): Option[UniProtProtein] =
      cm match {
        case p: UniProtProtein => Some(p)
        case _ => None
      }
  }
}
