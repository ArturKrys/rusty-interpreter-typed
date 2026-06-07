let m:cell(int) = cell(9);
let v:int = !m;
v+1;;

type Box = cell(int);
let m:Box = cell(0);
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

let c:int = 0;
let L:int = 1000;
let m:cell(int) = cell(c);
while (!m>0) {
    m := !m - 1
};
!m
;;

type Money = int;
let c:int = 0;
let L:Money = 1000;
let m:cell(int) = cell(c);
while (!m>0) {
    m := !m - 1
};
!m
;;

/* fails 
type Money = int;
let c:Money = 0;
type Money = bool;
let L:Money = 1000;
let m:cell(int) = cell(c);
while (!m>0) {
    m := !m - 1
};
!m
;;
*/

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
let m:cell(int) = cell(L);
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


type CALLBACK = int->int;
let sigfpe = cell ( |x:int|x );
let setfpe = |h:CALLBACK| { sigfpe := h };
let div = |n:int,m:int| {
      if (m==0) { (!sigfpe) (n) }
        else { n / m}
};
setfpe (|v:int| { -1 });
div (4) (2)
;;

type CALLBACK = int->int;
let sigfpe = cell ( |x:int|x );
let setfpe = |h:CALLBACK| { sigfpe := h };
let div = |n:int,m:int| {
      if (m==0) { (!sigfpe) (n) }
        else { n / m}
};
setfpe (|v:int| { -1 });
div (4) (0)
;;

let knot = cell (|x:int| {x});
let fact = |n:int| {
      if (n==0) { 1}
        else { n * ((!knot)( n - 1 ))}};
knot := fact;
fact (6)
;;

type CHURCH = cell(int)->cell(int)->cell(int);
let mkpair =
    | a:int,b:int | { 
        let l = cell(a);
        let r = cell(b);
        | f: CHURCH | { f (l) (r) }
}; 
let getfst = | p:CHURCH->cell(int) |
    { !(p ( |a:cell(int),b:cell(int)| { a })) };
let setfst = | p:CHURCH->cell(int),v:int |
    { (p (| a:cell(int),b:cell(int) | {a })) := v;()};
let getsnd = | p:CHURCH->cell(int) |
    { !(p (| a:cell(int),b:cell(int) | { b })) };
let setsnd = | p:CHURCH->cell(int),v:int |
    { (p (| a:cell(int),b:cell(int) | {b })) := v;()};
let x = mkpair(1, 2); 
setfst (x,10); 
setsnd (x,20); 
(getfst (x)) + (getsnd (x))
;;

type Opt = enum{#none:(()),#some:(int)};
let l0:Opt = #none(());
l0;;

type Opt = enum{#none:(()),#some:(int)};
let l0:Opt = #some(2);
l0;;

type Opt = enum { #none:(()), #some:(int)};
let l0:Opt = #none(());
match l0 {
    #none(x) => 1
|   #some(i) => i    
};;

type Opt = enum { #none:(()), #some:(int)};
let l0:Opt = #none(());
match l0 {
    #none(x) => 1
|   #some(i) => i    
};;

type Opt = enum { #none:(()), #some:(int)};
let l0:Opt = #none(());
match l0 {
    #none(x) => #some(2)
|   #some(i) => #none(())   
};;

type Opt = enum { #none:(()), #some:(int)};
let flop:Opt -> Opt = |o:Opt| {
    match o {
        #none(_) => #some(2)
    |   #some(i) => #none(()) 
    }
};
flop (#none(()))
;;

/* this ones fail to typecheck:

type Opt = enum { #none:(()), #some:(int)};
let l0:Opt = #some(42);
match l0 {
    #none(x) => 1
|   #some(i) => (i && true)  
};;

type Opt = enum { #none:(()), #some:(int)};
let l0:Opt = #some(42);
match l0 {
    #none(x) => 1
|   #some(i) => (i > 2)  
};;

type Opt = enum { #none:(()), #some:(int)};
let l0:Opt = #some(42);
match l0 {
    #none(x) => 1
};;

type Opt = enum { #none:(()), #some:(int)};
let l0:Opt = #some(42);
match l0 {
    #some(x) => x + 1
};;

*/

type LIST = enum { #NIL:(()), #CONS:(int,LIST)};
let hasnext:LIST -> bool = 
    |l:LIST| { match l {
            #CONS(x) => true
            | #NIL(_) => false
        }};
hasnext (#NIL(()));;

type LIST = enum { #NIL:(()), #CONS:(int,LIST)};
let hasnext:LIST -> bool = 
    |l:LIST| { match l {
            #CONS(x) => true
            | #NIL(_) => false
        }};
let l0:LIST = #CONS(2,#NIL(()));
hasnext (l0);;

/* recursive types */

type Nat = enum {
        #Z:(()),
        #S:(Nat)
    };
let Zero = #Z(());
let One = #S(Zero);
let Two = #S(One);
let int2Nat:int->Nat = |n:int| {
    if (n==0) { #Z(()) } else {#S(int2Nat (n-1))}
};
let add : Nat->Nat->Nat  = | n : Nat| | m: Nat | {
            match n {
                #Z(u) => m
            |   #S(n0) => #S(add (n0) (m))
        }
    };
add (int2Nat (4)) (int2Nat (6));;

type LIST = enum { #NIL:(()), #CONS:(int,LIST)};

let hasnext:LIST -> bool = 
    |l:LIST| { match l {
            #CONS(x,r) => true
            | #NIL(x) => false
        }};

let printlist:LIST -> () =
    |l:LIST| {
        let l0:cell(LIST) = cell(l);
        print!("[");
        while (hasnext(!l0)) {
            match !l0 {
                #CONS(p,r) =>   
                    print!(p);         
                    if hasnext(r) {
                        print!(", ");
                        l0 := r;
                        ()
                    } else {
                        println!("]");
                        l0 := r;
                        ()
                    }
            | #NIL(x) => ()
            }
        };
        ()
    };

let primes:cell(LIST) = cell(#NIL());

let checkprime:int->LIST->bool =
    |cp:int,lp:LIST| {
        match lp {
        #NIL() => true
        | #CONS(p,r) =>
                    if (cp == p*(cp/p))
                        { false }
                    else { checkprime (cp,r)}                
        }
    };

let primesieve =
    |n:int| { let c = cell(2);
          while (!c<n ){
             let cp = !c;
             if checkprime (cp,!primes) {
                primes := #CONS(cp,!primes);()
             } else {()};
             c:=cp+1
          }
    };

primesieve (1000);
printlist(!primes)
;;




/* mut */

let mut x:int = 10;
x+x;;

let mut x:int = 10;
x := x + 1;;


let mut x = 10;
let mut y = 5;
x+y;;

let mut x:int = 10;
let mut y:int = 5;
x := x + 1;
y := x + y;
x+y;;

/* examples from above, now with mutable lets
let L = 8;
let mut m = L;
let mut fp = 0;
let mut fnxt = 1;
while (m>0) {
    m := m - 1;
    {
        let t = fnxt;
        fnxt := fnxt + fp;
        fp := t
    }
};
fnxt
;;


/* panic */

let mut x:int = 10;
let f = { let mut p = 0; |z:int| { p + z } };
f (2);;

