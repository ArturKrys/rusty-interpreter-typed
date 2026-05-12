let m = cell(9);
let v = !m;
v+1;;

let m = cell(0);
m := !m + 1;
m := !m + 1;
m := !m + 1;
m := !m + 1;
m := !m + 1;
!m
;;

let m = cell(9);
let v = !m;
m := v + 1;
v
;;

let m = cell(9);
let v = !m;
m := v + 1;
!m
;;

let c = 0;
let L = 1000;
let m = cell(c);
while (!m>0) {
	m := !m - 1
};
!m
;;

let c = 0;
let L = 100000;
let m = cell(c);
while (!m>0) {
	m := !m - 1
};
!m
;;

let c = 0;
let L = 100;
let m = cell(L);
let S = cell(c);
while (!m>0) {
	m := !m - 1;
	S := !S + !m
};
!S
;;

let L = 8;
let m = cell(L);
let fp = cell(0);
let fnxt = cell(1);
while (!m>0) {
	m := !m - 1;
	{
		let t = !fnxt;
		fnxt := !fnxt + !fp;
		fp := t
	}
};
!fnxt
;;

let sigfpe = cell ( |x|x );
let setfpe = |h| { sigfpe := h };
let div = |n,m| {
	  if (m==0) { (!sigfpe) (n) }
		else { n / m}
};
setfpe (|v| { -1 });
div (4) (0)
;;

let sigfpe = cell ( |x|x );
let setfpe = |h| { sigfpe := h };
let div = |n,m| {
	  if (m==0) { (!sigfpe) (n) }
		else { n / m}
};
setfpe (|v| { -1 });
div (4) (2)
;;

let knot = cell (|x| {x});
let fact = |n| {
	  if (n==0) { 1}
		else { n * ((!knot)( n - 1 ))}};
knot := fact;
fact (6)
;;

let mkpair =
	| a,b | {
		let l = cell(a);
		let r = cell(b);
		| f | { f (l) (r) }
};
let getfst = | p | { p ( |a,b| { !a }) };
let setfst = | p,v | { p (| a,b | {a := v })};
let getsnd = | p | {p (| a,b | { !b })};
let setsnd = | p,v | { p (| a,b| { b := v })};
let x = mkpair(1, 2);
setfst (x,10);
setsnd (x,20);
(getfst (x)) + (getsnd (x))
;;

let hasnext =
	|l| { match l {
			#Cons(x,r) => true
			else => false
		}};
let printlist =
	|l| {
		let l0 = cell(l);
		print!("[");
		while (hasnext(!l0)) {
			match !l0 {
				#Cons(p,r) =>
					print!(p);
					if hasnext(r) {
						print!(", ");
						l0 := r
					} else {
						println!("]");
						l0 := r
					}
				else => ()
			}
		}
	};
let primes = cell(#Nil());
let checkprime =
	|cp,lp| {
		match lp {
		#Nil() => true
		else => match lp {
				#Cons(p,r) =>
					if (cp == p*(cp/p))
						{ false }
					else { checkprime (cp,r)}
				else => 0
				}
		}
	};
let primesieve =
	|n| { let c = cell(2);
		  while (!c<n ){
			 let cp = !c;
			 if checkprime (cp,!primes) {
				primes := #Cons(cp,!primes)
			 } else {()};
			 c:=cp+1
		  }
	};
primesieve (1000);
printlist(!primes)
;;
