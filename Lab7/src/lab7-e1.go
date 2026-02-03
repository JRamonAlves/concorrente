package main

import (
	"fmt"
	"math/rand"
)

func main() {
	join := make(chan int)
	ch := make(chan int)

	go criaNumeros(ch)
	go printaMaiorQue50(ch)
	<-join
}

func criaNumeros(ch chan int) {
	for {
		ch <- rand.Intn(100)
	}
}

func printaMaiorQue50(ch chan int) {
	for {
		x := <-ch
		if x > 50 {
			fmt.Printf("%d \n", x)
		}
	}
}
