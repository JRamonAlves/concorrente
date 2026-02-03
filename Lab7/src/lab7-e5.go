package main

import (
	"fmt"
	"math/rand"
)

func main() {

	CONSUMERS := 5

	join := make(chan int)
	ch1 := make(chan int, 100)
	ch2 := make(chan int, 100)

	go criaNumeros(ch1)
	go criaNumeros(ch2)

	for i := 0; i < CONSUMERS; i++ {
		if i%2 == 0 {
			go printaMaiorQue50(ch2, join, i)
		}
		else {
			go printaMaiorQue50(ch1, join, i)
		}
	}

	<-join
}

func criaNumeros(ch chan<- int) {
	for i := 0; i < rand.Intn(10000); i++ {
		ch <- rand.Intn(100)
	}
	ch <- -1
}

func printaMaiorQue50(ch <-chan int, join chan<- int, id int) {
	for x:= range ch {
		if x > 50 {
			fmt.Printf("Consumer %d recebeu o número %d \n", id, x)
		}
	}
}
