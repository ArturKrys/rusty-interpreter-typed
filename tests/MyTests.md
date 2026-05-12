/* Test 1 */ /* sum of first num of match */
let sum =
	|tuple| {
		match tuple {
			#Nil() => 0
			else =>
				match tuple {
					#Cons(num,innerTuple) => num + sum(innerTuple)
					else => -99999
				}
		}
	};

sum(
	#Cons(1,
		#Cons(2,
			#Cons(3,#Nil())
		)
	)
)
;;

/* Expected: 6 */
/* ----------------------------------------------------- */

/* Test 2 */


/* Expected:   */
/* ----------------------------------------------------- */

/* Test 3 */


/* Expected:   */
/* ----------------------------------------------------- */

/* Test 4 */


/* Expected:   */
/* ----------------------------------------------------- */

/* Test 5 */


/* Expected:   */
/* ----------------------------------------------------- */
