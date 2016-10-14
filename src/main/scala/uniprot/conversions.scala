package com.bio4j.data.uniprot

import com.bio4j.model._
import com.bio4j.angulillos._
import scala.xml._
import scala.compat.java8.OptionConverters._

case object conversions {

  val stringToGeneLocation: String => UniProtGraph.GeneLocations =
    {
      case "apicoplast"                   => UniProtGraph.GeneLocations.apicoplast
      case "chloroplast"                  => UniProtGraph.GeneLocations.chloroplast
      case "organellar chromatophore"     => UniProtGraph.GeneLocations.organellar_chromatophore
      case "cyanelle"                     => UniProtGraph.GeneLocations.cyanelle
      case "hydrogenosome"                => UniProtGraph.GeneLocations.hydrogenosome
      case "mitochondrion"                => UniProtGraph.GeneLocations.mitochondrion
      case "non-photosynthetic plastid"   => UniProtGraph.GeneLocations.non_photosynthetic_plastid
      case "nucleomorph"                  => UniProtGraph.GeneLocations.nucleomorph
      case "plasmid"                      => UniProtGraph.GeneLocations.plasmid
      case "plastid"                      => UniProtGraph.GeneLocations.plastid
      case _                              => UniProtGraph.GeneLocations.chromosome
    }

  val stringToCommentTopic: String => UniProtGraph.CommentTopics =
    {
      case "allergen"                       =>  UniProtGraph.CommentTopics.allergen
      case "alternative products"           =>  UniProtGraph.CommentTopics.alternativeProducts
      case "biotechnology"                  =>  UniProtGraph.CommentTopics.biotechnology
      case "biophysicochemical properties"  =>  UniProtGraph.CommentTopics.biophysicochemicalProperties
      case "catalytic activity"             =>  UniProtGraph.CommentTopics.catalyticActivity
      case "caution"                        =>  UniProtGraph.CommentTopics.caution
      case "cofactor"                       =>  UniProtGraph.CommentTopics.cofactor
      case "developmental stage"            =>  UniProtGraph.CommentTopics.developmentalStage
      case "disease"                        =>  UniProtGraph.CommentTopics.disease
      case "domain"                         =>  UniProtGraph.CommentTopics.domain
      case "disruption phenotype"           =>  UniProtGraph.CommentTopics.disruptionPhenotype
      case "enzyme regulation"              =>  UniProtGraph.CommentTopics.enzymeRegulation
      case "function"                       =>  UniProtGraph.CommentTopics.function
      case "induction"                      =>  UniProtGraph.CommentTopics.induction
      case "miscellaneous"                  =>  UniProtGraph.CommentTopics.miscellaneous
      case "pathway"                        =>  UniProtGraph.CommentTopics.pathway
      case "pharmaceutical"                 =>  UniProtGraph.CommentTopics.pharmaceutical
      case "polymorphism"                   =>  UniProtGraph.CommentTopics.polymorphism
      case "PTM"                            =>  UniProtGraph.CommentTopics.PTM
      case "RNA editing"                    =>  UniProtGraph.CommentTopics.RNAEditing
      case "similarity"                     =>  UniProtGraph.CommentTopics.similarity
      case "subcellular location"           =>  UniProtGraph.CommentTopics.subcellularLocation
      case "sequence caution"               =>  UniProtGraph.CommentTopics.sequenceCaution
      case "subunit"                        =>  UniProtGraph.CommentTopics.subunit
      case "tissue specificity"             =>  UniProtGraph.CommentTopics.tissueSpecificity
      case "toxic dose"                     =>  UniProtGraph.CommentTopics.toxicDose
      case "online information"             =>  UniProtGraph.CommentTopics.onlineInformation
      case "mass spectrometry"              =>  UniProtGraph.CommentTopics.massSpectrometry
      case "interaction"                    =>  UniProtGraph.CommentTopics.interaction
    }

  val stringToFeatureType: String => UniProtGraph.FeatureTypes =
    {
      case "active site"                          => UniProtGraph.FeatureTypes.activeSite
      case "binding site"                         => UniProtGraph.FeatureTypes.bindingSite
      case "calcium-binding region"               => UniProtGraph.FeatureTypes.calciumBindingRegion
      case "chain"                                => UniProtGraph.FeatureTypes.chain
      case "coiled-coil region"                   => UniProtGraph.FeatureTypes.coiledCoilRegion
      case "compositionally biased region"        => UniProtGraph.FeatureTypes.compositionallyBiasedRegion
      case "cross-link"                           => UniProtGraph.FeatureTypes.crosslink
      case "disulfide bond"                       => UniProtGraph.FeatureTypes.disulfideBond
      case "DNA-binding region"                   => UniProtGraph.FeatureTypes.DNABindingRegion
      case "domain"                               => UniProtGraph.FeatureTypes.domain
      case "glycosylation site"                   => UniProtGraph.FeatureTypes.glycosylationSite
      case "helix"                                => UniProtGraph.FeatureTypes.helix
      case "initiator methionine"                 => UniProtGraph.FeatureTypes.initiatorMethionine
      case "lipid moiety-binding region"          => UniProtGraph.FeatureTypes.lipidMoietyBindingRegion
      case "metal ion-binding site"               => UniProtGraph.FeatureTypes.metalIonBindingSite
      case "modified residue"                     => UniProtGraph.FeatureTypes.modifiedResidue
      case "mutagenesis site"                     => UniProtGraph.FeatureTypes.mutagenesisSite
      case "non-consecutive residues"             => UniProtGraph.FeatureTypes.nonConsecutiveResidues
      case "non-terminal residue"                 => UniProtGraph.FeatureTypes.nonTerminalResidue
      case "nucleotide phosphate-binding region"  => UniProtGraph.FeatureTypes.nucleotidePhosphateBindingRegion
      case "peptide"                              => UniProtGraph.FeatureTypes.peptide
      case "propeptide"                           => UniProtGraph.FeatureTypes.propeptide
      case "region of interest"                   => UniProtGraph.FeatureTypes.regionOfInterest
      case "repeat"                               => UniProtGraph.FeatureTypes.repeat
      case "non-standard amino acid"              => UniProtGraph.FeatureTypes.nonstandardAminoAcid
      case "sequence conflict"                    => UniProtGraph.FeatureTypes.sequenceConflict
      case "sequence variant"                     => UniProtGraph.FeatureTypes.sequenceVariant
      case "short sequence motif"                 => UniProtGraph.FeatureTypes.shortSequenceMotif
      case "signal peptide"                       => UniProtGraph.FeatureTypes.signalPeptide
      case "site"                                 => UniProtGraph.FeatureTypes.site
      case "splice variant"                       => UniProtGraph.FeatureTypes.spliceVariant
      case "strand"                               => UniProtGraph.FeatureTypes.strand
      case "topological domain"                   => UniProtGraph.FeatureTypes.topologicalDomain
      case "transit peptide"                      => UniProtGraph.FeatureTypes.transitPeptide
      case "transmembrane region"                 => UniProtGraph.FeatureTypes.transmembraneRegion
      case "turn"                                 => UniProtGraph.FeatureTypes.turn
      case "unsure residue"                       => UniProtGraph.FeatureTypes.unsureResidue
      case "zinc finger region"                   => UniProtGraph.FeatureTypes.zincFingerRegion
      case "intramembrane region"                 => UniProtGraph.FeatureTypes.intramembraneRegion
    }

  val stringToKeywordCategory: String => UniProtGraph.KeywordCategories =
    {
      case "Biological process"         => UniProtGraph.KeywordCategories.biologicalProcess
      case "Cellular component"         => UniProtGraph.KeywordCategories.cellularComponent
      case "Coding sequence diversity"  => UniProtGraph.KeywordCategories.codingSequenceDiversity
      case "Developmental stage"        => UniProtGraph.KeywordCategories.developmentalStage
      case "Disease"                    => UniProtGraph.KeywordCategories.disease
      case "Domain"                     => UniProtGraph.KeywordCategories.domain
      case "Ligand"                     => UniProtGraph.KeywordCategories.ligand
      case "Molecular function"         => UniProtGraph.KeywordCategories.molecularFunction
      case "null"                       => UniProtGraph.KeywordCategories.NULL
      case "PTM"                        => UniProtGraph.KeywordCategories.PTM
      case "Technical term"             => UniProtGraph.KeywordCategories.technicalTerm
    }
}
