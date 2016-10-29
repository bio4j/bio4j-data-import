package com.bio4j.data.uniprot

import com.bio4j.model._
import com.bio4j.angulillos._
import scala.compat.java8.OptionConverters._
import bio4j.data.uniprot._

case object conversions {

  def statusToDatasets(status: Status): UniProtGraph.Datasets =
    status match {
      case Reviewed   => UniProtGraph.Datasets.swissProt
      case Unreviewed => UniProtGraph.Datasets.trEMBL
    }

  def proteinExistenceToExistenceEvidence(prEx: ProteinExistence): UniProtGraph.ExistenceEvidence =
    prEx match {
      case EvidenceAtProteinLevel     => UniProtGraph.ExistenceEvidence.proteinLevel
      case EvidenceAtTranscriptLevel  => UniProtGraph.ExistenceEvidence.transcriptLevel
      case InferredFromHomology       => UniProtGraph.ExistenceEvidence.homologyInferred
      case Predicted                  => UniProtGraph.ExistenceEvidence.predicted
      case Uncertain                  => UniProtGraph.ExistenceEvidence.uncertain
    }

  def organelleToGeneLocation(org: Organelle): UniProtGraph.GeneLocations =
    org match {
      case Apicoplast               => UniProtGraph.GeneLocations.apicoplast
      case Chloroplast              => UniProtGraph.GeneLocations.chloroplast
      case OrganellarChromatophore  => UniProtGraph.GeneLocations.organellar_chromatophore
      case Cyanelle                 => UniProtGraph.GeneLocations.cyanelle
      case Hydrogenosome            => UniProtGraph.GeneLocations.hydrogenosome
      case Mitochondrion            => UniProtGraph.GeneLocations.mitochondrion
      case NonPhotosyntheticPlastid => UniProtGraph.GeneLocations.non_photosynthetic_plastid
      case Nucleomorph              => UniProtGraph.GeneLocations.nucleomorph
      case Plasmid(_)               => UniProtGraph.GeneLocations.plasmid
      case Plastid                  => UniProtGraph.GeneLocations.plastid
    }

  def featureKeyToFeatureType(ftKey: FeatureKey): UniProtGraph.FeatureTypes =
    ftKey match {
      case INIT_MET   => UniProtGraph.FeatureTypes.initiatorMethionine
      case SIGNAL     => UniProtGraph.FeatureTypes.signalPeptide
      case PROPEP     => UniProtGraph.FeatureTypes.propeptide
      case TRANSIT    => UniProtGraph.FeatureTypes.transitPeptide
      case CHAIN      => UniProtGraph.FeatureTypes.chain
      case PEPTIDE    => UniProtGraph.FeatureTypes.peptide
      case TOPO_DOM   => UniProtGraph.FeatureTypes.topologicalDomain
      case TRANSMEM   => UniProtGraph.FeatureTypes.transmembraneRegion
      case INTRAMEM   => UniProtGraph.FeatureTypes.intramembraneRegion
      case DOMAIN     => UniProtGraph.FeatureTypes.domain
      case REPEAT     => UniProtGraph.FeatureTypes.repeat
      case CA_BIND    => UniProtGraph.FeatureTypes.calciumBindingRegion
      case ZN_FING    => UniProtGraph.FeatureTypes.zincFingerRegion
      case DNA_BIND   => UniProtGraph.FeatureTypes.DNABindingRegion
      case NP_BIND    => UniProtGraph.FeatureTypes.nucleotidePhosphateBindingRegion
      case REGION     => UniProtGraph.FeatureTypes.regionOfInterest
      case COILED     => UniProtGraph.FeatureTypes.coiledCoilRegion
      case MOTIF      => UniProtGraph.FeatureTypes.shortSequenceMotif
      case COMPBIAS   => UniProtGraph.FeatureTypes.compositionallyBiasedRegion
      case ACT_SITE   =>  UniProtGraph.FeatureTypes.activeSite
      case METAL      => UniProtGraph.FeatureTypes.metalIonBindingSite
      case BINDING    => UniProtGraph.FeatureTypes.bindingSite
      case SITE       => UniProtGraph.FeatureTypes.site
      case NON_STD    => UniProtGraph.FeatureTypes.nonstandardAminoAcid
      case MOD_RES    => UniProtGraph.FeatureTypes.modifiedResidue
      case LIPID      => UniProtGraph.FeatureTypes.lipidMoietyBindingRegion
      case CARBOHYD   => UniProtGraph.FeatureTypes.glycosylationSite
      case DISULFID   => UniProtGraph.FeatureTypes.disulfideBond
      case CROSSLNK   => UniProtGraph.FeatureTypes.crosslink
      case VAR_SEQ    => UniProtGraph.FeatureTypes.spliceVariant
      case VARIANT    => UniProtGraph.FeatureTypes.sequenceVariant
      case MUTAGEN    => UniProtGraph.FeatureTypes.mutagenesisSite
      case UNSURE     => UniProtGraph.FeatureTypes.unsureResidue
      case CONFLICT   => UniProtGraph.FeatureTypes.sequenceConflict
      case NON_CONS   => UniProtGraph.FeatureTypes.nonConsecutiveResidues
      case NON_TER    => UniProtGraph.FeatureTypes.nonTerminalResidue
      case HELIX      => UniProtGraph.FeatureTypes.helix
      case STRAND     => UniProtGraph.FeatureTypes.strand
      case TURN       => UniProtGraph.FeatureTypes.turn
    }

  def commentTopic(c: Comment): UniProtGraph.CommentTopics =
    c match {
      case _ => ???
    }



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
