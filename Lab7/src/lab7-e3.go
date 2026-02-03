package main

import (
	"fmt"
	"math/rand"
)

func main() {
	join := make(chan int)
	ch := make(chan int)

	go criaNumeros(ch)
	go criaNumeros(ch)
	go printaMaiorQue50(ch, join)
	<-join
}

func criaNumeros(ch chan int) {
	for i := 0; i < rand.Intn(10000); i++ {
		ch <- rand.Intn(100)
	}
	ch <- -1
}

func printaMaiorQue50(ch chan int, join chan int) {

	interrup := 0

	for {
		x := <-ch

		if x == -1 {
			interrup += 1
			if interrup == 2 {
				break
			}
		}

		if x > 50 {
			fmt.Printf("%d \n", x)
		}
	}

	// close(ch)
	join <- 0
}
