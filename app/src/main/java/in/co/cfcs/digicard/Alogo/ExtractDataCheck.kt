package `in`.co.cfcs.digicard.Alogo

 class ExtractDataCheck(){


     companion object {
         fun DesignationCheck(firstLine: String): Boolean {

             if (firstLine.contains("Chief Executive Officer") || firstLine.contains("Chief Operating Officer") || firstLine.contains("Chief Financial Officer") ||
                 firstLine.contains("Chief Technology Officer") || firstLine.contains("Chief Marketing Officer") || firstLine.contains("Chief Legal Officer") ||
                 firstLine.contains("Accounts Manager") || firstLine.contains("Recruitment Manager") || firstLine.contains("Technology Manager") ||
                 firstLine.contains("Store Manager") || firstLine.contains("Regional Manager") || firstLine.contains("Functional Manager") ||
                 firstLine.contains("Departmental Managers") || firstLine.contains("General Managers") || firstLine.contains("CEO") ||
                 firstLine.contains("COO") || firstLine.contains("Departmental Heads") || firstLine.contains("Director") || firstLine.contains("Chairman and Managing Director") || firstLine.contains("CMD")
                 || firstLine.contains("Departmental Deputy Heads or General Managers") || firstLine.contains("Manager") || firstLine.contains("Sub-ordinates") || firstLine.contains("Team Manager")
                 || firstLine.contains("Sales") || firstLine.contains("Engineer") ||  firstLine.contains("MANAGER") ||  firstLine.contains("SALE") || firstLine.contains("ACCOUNTS")
                 || firstLine.contains("SENIOR") || firstLine.contains("Senior") || firstLine.contains("Junior") || firstLine.contains("JUNIOR") || firstLine.contains("President")
                 || firstLine.contains("President") || firstLine.contains("Head") || firstLine.contains("VP"))
             {
                 return true
             }

             return false
         }

          fun CommunityCheck(firstLine: String): Boolean {

             if (firstLine.contains("PVT.") || firstLine.contains("LTD.") || firstLine.contains("LTD") ||
                 firstLine.contains("S.r.l.") || firstLine.contains("eurl") || firstLine.contains("GbR") ||
                 firstLine.contains("GmbH") || firstLine.contains("inc") || firstLine.contains("LLC") ||
                 firstLine.contains("LLP") || firstLine.contains("Ltd") || firstLine.contains("Srl") ||
                 firstLine.contains("Corp") || firstLine.contains("Ltd.") || firstLine.contains("Inc.") ||
                 firstLine.contains("Co,LTD.") || firstLine.contains("Pvt.") || firstLine.contains("PRIVATE") || firstLine.contains("LIMITED"))
             {
                 return true
             }

             return false
         }


         fun AddressCheck(firstLine: String): Boolean {

             if (firstLine.contains("Address") || firstLine.contains("ADDRESS") || firstLine.contains("P.O.BOX") ||
                 firstLine.contains("P.O.Box") || firstLine.contains("Block No.") || firstLine.contains("BLOCK NO.") ||
                 firstLine.contains("Block No") || firstLine.contains("Office No.") || firstLine.contains("OFFICE NO") ||
                 firstLine.contains("Floor") || firstLine.contains("floor") || firstLine.contains("Plot No.") ||
                 firstLine.contains("Sector") || firstLine.contains("Road") || firstLine.contains("Plot")

//                || firstLine.contains("Co,LTD.") || firstLine.contains("Pvt.") || firstLine.contains("PRIVATE") || firstLine.contains("LIMITED")
             )
             {
                 return true
             }

             return false
         }


     }




}

