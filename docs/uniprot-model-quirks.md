# UniProt model

## Genes

- Gene names are far from unique. For example, there are 12 entries (thus at leasat 12 isoforms) with primary gene name `APP`.
- *Different* genes can be connected with the same protein. Quoting from [Uniprot help - gene name](http://www.uniprot.org/help/gene_name):

    > It can happen that multiple genes can be translated to produce identical proteins in one species. In such cases, all gene products were historically often merged into a single UniProtKB entry and there are as many ‘Name’ tokens in the ‘Gene names’ subsection as the number of genes encoding the protein of interest, e.g. [P68431](http://www.uniprot.org/uniprot/P68431#names_and_taxonomy). However, we tend to demerge many of these entries, and for newly annotated proteins we generate separate sequence entries in case of multiple genes coding for identical protein sequences, e.g. [P08409](http://www.uniprot.org/uniprot/?query=replaces:P08409).

    this means that in the data there will be several gene name elements.

See also

- [UniProt help - gene symbols to UniProt identifiers](http://www.uniprot.org/help/gene_symbol_mapping)
- [UniProt help - mappings to NCBI GeneID and RefSeq ](http://www.uniprot.org/help/ncbi_mappings)

## Isoforms

- All isoforms are declared at least once as part of an entry.
- The same isoform can be the canonical isoform for one entry, and a secondary one in another
- the "isoform of" relationship is not transitive; see [P42166](http://www.uniprot.org/uniprot/P42166#sequences) and [P42167](http://www.uniprot.org/uniprot/P42167#sequences) for example.
- Most annotations inside the entry refer to isoforms by name, not id. This means that in most cases comments just doesn't make any sense by themselves. See for example http://www.uniprot.org/uniprot/P04150.xml. Most of them do not have any connection to the isoform they refer to, only as part of the comment text. In other cases, there is a `<molecule>` element, but the isoform to which it refers is just written there like `"Isoform Alpha-B"`, not using its id.
- Differences between isoform sequences are not provided in any reasonable format, just as text.
- As part of features, there's alternative sequence, which is related with isoforms: [Alternative sequence](http://www.uniprot.org/help/alternative_products). This could useful for linking sequence variations to isoforms.

Their documentation is contradictory at best. See

- [What is the canonical sequence? Are all isoforms described in one entry?](http://www.uniprot.org/help/canonical_and_isoforms)
- [Alternative products](http://www.uniprot.org/help/alternative_products)

## Keywords, subcellular locations, and GO

For now see

- [Differences between keywords and GO terms](http://www.uniprot.org/help/keywords_vs_go)
- [Keywords to GO terms mapping](http://geneontology.org/external2go/uniprotkb_kw2go)
- [Subcellular locations to GO terms mapping](http://geneontology.org/external2go/uniprotkb_sl2go)
- [Subcellular locations docs](http://www.uniprot.org/docs/subcell)
- [Subcellular locations help](http://www.uniprot.org/help/subcellular_location)

## General references

- [UniProt user manual](ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/complete/docs/userman.htm)
