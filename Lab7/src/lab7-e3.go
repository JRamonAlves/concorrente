package main

import (
	"fmt"
	"math/rand"
)

func main() {
	join := make(chan int)
	ch1 := make(chan int, 100)
	ch2 := make(chan int, 100)

	go criaNumeros(ch1)
	go criaNumeros(ch2)
	go printaMaiorQue50(ch1, ch2, join)
	<-join
}

func criaNumeros(ch chan int) {
	for i := 0; i < rand.Intn(10000); i++ {
		ch <- rand.Intn(100)
	}
	close(ch)
}

func printaMaiorQue50(ch1 chan int, ch2 chan int, join chan int) {
	for x := range ch1 {

		if x > 50 {
			fmt.Printf("%d \n", x)
		}
	}

	for x := range ch2 {

		if x > 50 {
			fmt.Printf("%d \n", x)
		}
	}

	join <- 0
}
