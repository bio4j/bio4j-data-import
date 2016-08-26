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

First, it's nowhere documented (that I could find) what actually goes in the GO database cross-reference.

About subcellular locations, when different isoforms have different locations, they are reported incorrectly as if they would correspond to both isoforms: see for example [O43687](http://www.uniprot.org/uniprot/O43687#subcellular_location). Note that this is also wrong in the complete GO annotation data, and thus, I presume, in UniProtKB-GOA data.

This is correctly represented in the subcellular location comment, but the format is awful, refering to the corresponding isoform *by its name* in the `<molecule>` element. So the obvious question is: to *what* should this correspondence be assigned? the canonical isoform? all of them?

In general, the UniProtKB-GOA data seems to be in a better state.

For now see

- [Differences between keywords and GO terms](http://www.uniprot.org/help/keywords_vs_go)
- [Keywords to GO terms mapping](http://geneontology.org/external2go/uniprotkb_kw2go)
- [Subcellular locations to GO terms mapping](http://geneontology.org/external2go/uniprotkb_sl2go)
- [Subcellular locations docs](http://www.uniprot.org/docs/subcell)
- [Subcellular locations help](http://www.uniprot.org/help/subcellular_location)

## ENZYME annotations

Another nightmare. Again, the root of all evil lies in conflating different functional products (domains, for example) into one entity.

### "Partial" EC ids

See http://www.uniprot.org/help/2007/10/23/release. Quoting from there:

>  In the UniProt Knowledgebase some enzymes are assigned so-called partial EC numbers where part of the numbers are replaced by dashes (e.g. EC 3.4.24.-). This happens in the following situations:
>
>  1. The catalytic activity of the protein is not known exactly.
>  2. The protein catalyzes a reaction that is known, but not yet included in the IUBMB EC list.

Now compare that with [P15291 xml](http://www.uniprot.org/uniprot/P15291.xml). There we have

- `<ecNumber>2.4.1.-</ecNumber>` for the `<protein>` element
- *Precise* enzyme codes for domains, which are just names for the protein: `<ecNumber>2.4.1.22</ecNumber>` for `Lactose synthase A protein`, or `<ecNumber>2.4.1.90</ecNumber>` for `N-acetyllactosamine synthase`

Then as cross-reference to ENZYME we have all of them together, with no scope or anything

``` xml
<dbReference type="EC" id="2.4.1.-"/>
<dbReference type="EC" id="2.4.1.22"/>
<dbReference type="EC" id="2.4.1.90"/>
<dbReference type="EC" id="2.4.1.38"/>
```

If you search for the ENZYME cross-ref description in [this somewhat hidden flat-text file](http://www.uniprot.org/docs/dbxref) you will read there "*Implicit; All UniProtKB entries that contain >=1 EC numbers in their description lines*".

So clearly this is not any of 1., 2. quoted above; it's just a bad data model where entities are hidden behind strings.

Also, a small interlude for subcellular locations: one of these "domains" (remember, they're just that: a name) can be the text of a subcellular location comment:

``` xml
<comment type="subcellular location">
  <molecule>Processed beta-1,4-galactosyltransferase 1</molecule>
  <subcellularLocation>
    <location evidence="14">Secreted</location>
  </subcellularLocation>
  <text evidence="14">Soluble form found in body fluids.</text>
</comment>
```

All in all, these enzyme annotations are unreliable, badly modeled, and hard to interpret. Our choice is to take them from the cross-reference data, dropping the so-called partial IDs, and associate them to the canonical protein of the corresponding entry. The only feasible improvement I see on this is to add new edge types which let you link proteins to Enzyme classes, subclasses, subsubclasses; of course, we need to include all this into ENZYME first.

## General references

- [UniProt user manual](ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/complete/docs/userman.htm)
