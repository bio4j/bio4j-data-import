# Data import architecture

All import code here is written generically using bio4j/angulillos; this means that it can be used to import data on any graph with a supporting angulillos implementation, such as bio4j/angulillos-titan.

Each import data class has as arguments all the data files it needs.

For all graphs we import all vertices first, and then the edges. Dependencies between graphs, like those in the case where a graph connects vertices of two different graphs, are explicit, as constructor arguments of the corresponding import data class.

## GO

We load the whole thing in memory, use simple XPATH expressions. Their schema is not expressive enough to be useful.

## UniProt

We read the super big xml file line by line, and group it lazily into entries, using a sliding iterator with a tailrec accum.

Now for each entry, we use XPATH directly.
