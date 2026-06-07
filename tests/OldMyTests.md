/* ----------------------------------------------------- */
/* Test 1 */
/*
 * sum of first num of match
 * PS.: there is better ways to test this I just wanted a match isnide a match
 */
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
/*
 * Before the assignment, !(!b) would correctly give 1.
 * After b := 99, the store at b's location holds VInt(99), not VCell anymore.
 * !(!b) should be KO
 */

let a = cell(1);
let b = cell(a);
b := 99;
!(!b)
;;

/* Expected: deref: cell expected, found: interpreter.values.VInt@266474c2  */

/* ----------------------------------------------------- */
/* Test 3 */
/*
 * String Concatenation mixed with Struct Tuples
 */
let greet = |person| {
	match person {
		#Person(name, age) => "Hello " + name + ", age " + age
		else => "Unknown Entity"
	}
};

greet(#Person("Alice", 30))
;;

/* Expected: "Hello Alice, age 30" */

/* ----------------------------------------------------- */
/* Test 4 */
/*
 * Recursive Map function over a Struct Tuple Linked List
 */
let map = |list, f| {
	match list {
		#Nil() => #Nil()
		else =>
			match list {
				#Cons(head, tail) => #Cons(f(head), map(tail, f))
				else => #Nil()
			}
	}
};

let double = |x| { x * 2 };
map(
	#Cons(10,
		#Cons(20, #Nil())
	),
	double
)
;;

/* Expected: #Cons(20, #Cons(40, #Nil())) */

/* ----------------------------------------------------- */
/* Test 5 */
/*
 * Iterative List Length using Cells and While loop
 */
let listLength = |lst| {
	let current = cell(lst);
	let count = cell(0);
	let looping = cell(true);

	while (!looping == true) {
		match !current {
			#Nil() => {
				looping := false
			}
			else =>
				match !current {
					#Cons(val, next) => {
						count := !count + 1;
						current := next
					}
					else => {
						looping := false
					}
				}
		}
	};
	!count
};

listLength(
	#Cons(1,
		#Cons(2,
			#Cons(3,
				#Cons(4,
					#Nil()
				)
			)
		)
	)
)
;;

/* Expected: 4 */

/* ----------------------------------------------------- */
