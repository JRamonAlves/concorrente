package main

import (
	"fmt"
	"math/rand"
)

func main() {
	join := make(chan int)
	ch := make(chan int)

	go criaNumeros(ch)
	go printaMaiorQue50(ch, join)
	<-join
}

func criaNumeros(ch chan int) {
	for i := 0; i < 10_000; i++ {
		ch <- rand.Intn(100)
	}
	ch <- -1
	close(ch)
}

func printaMaiorQue50(ch chan int, join chan int) {
	for {
		x := <-ch

		if x == -1 {
			break
		}

		if x > 50 {
			fmt.Printf("%d \n", x)
		}
	}
	join <- 0
}
