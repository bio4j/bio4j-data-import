// package com.bio4j.data.test
//
// import org.scalatest.FunSuite
// import scala.xml._
// import com.bio4j.data._, enzyme._
//
// class EnzymeParsingTests extends FunSuite {
//
//   test("Enzyme classes parsing") {
//
//     val lines = io.Source.fromFile("enzclass.txt").getLines
//
//     val enzymeClasses: Seq[EnzymeClass] = EnzymeClass fromLines lines.toSeq
//
//     println { enzymeClasses.length }
//
//     val firstID =
//       enzymeClasses.head.ID
//
//     val firstDescription =
//       enzymeClasses.head.description
//
//     assert { firstID == "1.-.-.-" }
//     assert { firstDescription == "Oxidoreductases" }
//   }
//
//   test("Enzyme entry parsing") {
//
//     val lines = io.Source.fromFile("enzyme.dat").getLines
//
//     val enzymeEntries: Seq[Entry] = Entry validEntriesFromLines lines.toSeq
//
//     // enzymeEntries.foreach { e =>
//     //   println { s"id: ${e.ID}, description: ${e.description}" }
//     // }
//
//     val firstEntry = enzymeEntries.head
//
//     assert { firstEntry.ID == "1.1.1.1" }
//     assert { firstEntry.description == "Alcohol dehydrogenase" }
//
//     enzymeEntries foreach {e => println(printEntry(e)) }
//
//     def printEntry(e: Entry): String =
//       s"""
//       id: ${e.ID}
//       description: ${e.description}
//       alternative names: ${e.alternativeNames}
//       cofactors: ${e.cofactors}
//       catalytic activity: ${e.catalyticActivity}
//       comments: ${e.comments}
//       """
//   }
// }
