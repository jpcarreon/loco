HAI
	I HAS A number ITZ 8
	I HAS A row ITZ 0
	I HAS A col ITZ 0
	I HAS A mid
	I HAS A temp ITZ 0

	BOTH SAEM MOD OF number AN 2 AN 0
	O RLY?
		YA RLY
			number R DIFF OF number AN 1
	OIC

	mid R QUOSHUNT OF number AN 2

	IM IN YR printRowDiamond UPPIN YR row WILE DIFFRINT row AN BIGGR OF row AN number
		IM IN YR printColDiamond UPPIN YR col WILE DIFFRINT col AN BIGGR OF col AN number
			BOTH SAEM col AN SMALLR OF col AN SUM OF mid AN temp 
			O RLY?
				YA RLY
					BOTH SAEM col AN BIGGR OF col AN DIFF OF mid AN temp
					O RLY?
						YA RLY
							VISIBLE "*" !
						NO WAI
							VISIBLE " " !
					OIC
				NO WAI
					VISIBLE " " !
			OIC

		IM OUTTA YR printColDiamond
		VISIBLE ""
		col R 0

		BOTH SAEM row AN BIGGR OF row AN mid
		O RLY?
			YA RLY
				temp R DIFF OF temp AN 1
			NO WAI
				temp R SUM OF temp AN 1
		OIC
		
	IM OUTTA YR printRowDiamond
KTHXBYE
