import { Component, OnInit } from '@angular/core';
import * as CryptoJS from 'crypto-js';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { HttpClient, HttpParams } from "@angular/common/http";
// @ts-ignore
import { saveAs } from 'file-saver';


const SERVER = "http://localhost:8081";

export interface Card {
  accountNumber: string;
  balance: number;
  cvv: string;
  pin: string;
}

export interface OperationRequest {
  accountNumber: string;
  amount: number;
}

export interface WithdrawResponse {
  status: boolean;
  updatedBalance: number;
  message: string;
}

export interface DepositResponse {
  status: boolean;
  updatedBalance: number;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  isAuthorized: boolean;
  fileContent!: string;
  balance: number;
  CVV!: string;
  PVV!: string;
  cardNumber!: string;
  accountNumber!: string;
  isCardLoaded!: boolean;
  isCardCreated!: boolean;
  pinFromCreation!: string;
  operationMessage!: string;
  title = 'atm';
  atmGroup!: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private httpClient: HttpClient
  ) {
    this.isAuthorized = false;
    this.balance = 0;
    // CryptoJS.SHA256(string);
  }

  ngOnInit(): void {
    this.atmGroup = this.formBuilder.group({
      'cardFile': [''],
      'amount': [0],
      'PIN': ['', [Validators.required, Validators.maxLength(4)]]
    });
  }


  displayFG() {
    console.log(this.atmGroup);
  }

  onCardLoaded($event: Event) {
    const inputElement = $event.target as HTMLInputElement;
    const file = inputElement.files?.[0];
    if (file) {
      const reader = new FileReader();

      reader.readAsText(file);

      reader.onload = () => {
        const fileContent: string = reader.result as string;
        console.log(fileContent);
        if (fileContent) {
          const [cvv, balance, accountNumber] = fileContent.split(":");
          this.CVV = cvv;
          this.balance = Number(balance);
          this.accountNumber = accountNumber;
          this.isCardLoaded = true;
          this.isCardCreated = true;
        }


        // state.cardData.cvv = cvv;
        // state.cardData.balance = balance;
        // balanceEl.innerHTML = state.cardData.balance;
        // state.isLoaded = true;
        // handleVisibilityState(pinCodeBlock, state.isLoaded);
      };
    }

  }

  createCard() {
    this.httpClient.get<Card>(SERVER + '/atm/create-card').subscribe(card => {
      console.log(card);
      if (card) {
        this.CVV = card.cvv;
        this.balance = card.balance;
        this.isCardLoaded = true;
        this.isCardCreated = true;
        this.accountNumber = card.accountNumber;
        this.pinFromCreation = card.pin;
        const cardContent = `${this.CVV}:${this.balance}:${this.accountNumber}`;
        this.saveToFile('card.txt', cardContent);
      }
    });
  }

  authorizeinATM() {
    const PVV = this.generatePVV();
    this.PVV = PVV;
    const data = {
      pvv : this.PVV,
      cvv : this.CVV
    };

    this.httpClient.post(SERVER + '/atm/authorize-card', data).subscribe(result => {
      if (result) {
        this.isAuthorized = true;
      } else {
        this.isAuthorized = false;
      }
    })
  }

  generatePVV(): string {
    const pin = this.atmGroup.get('PIN')?.value;
    const PVV = this.tripleHash(pin);
    return PVV;
  }

  tripleHash(value: string): string {
    const hash = this.hash(this.hash(this.hash(value)));
    const secondHalf = hash.substring(hash.length / 2 - 1);
    return secondHalf;
  }

  hash(value: string): string {
    return CryptoJS.SHA256(value).toString(CryptoJS.enc.Hex);
  }

  saveToFile(fileName: string, content: string) {
    const blob = new Blob([content], { type: 'text/plain;charset=utf-8' });
    saveAs(blob, fileName);
  }

  withdraw() {
    const amount = this.atmGroup.get('amount')?.value;
    const withdrawOperation: OperationRequest = {
      accountNumber: this.accountNumber,
      amount: Number(amount)
    };
    this.httpClient.post<WithdrawResponse>(SERVER + '/atm/withdraw', withdrawOperation).subscribe(response => {
      if (response.status) {
        this.balance = response.updatedBalance;
        this.operationMessage = '';
      } else {
        this.operationMessage = response.message;
      }
    })
  }

  deposit() {
    const amount = this.atmGroup.get('amount')?.value;
    const depositOperation: OperationRequest = {
      accountNumber: this.accountNumber,
      amount: Number(amount)
    };

    this.httpClient.post<DepositResponse>(SERVER + '/atm/deposit', depositOperation).subscribe(response => {
      if (response.status) {
        this.balance = response.updatedBalance;
        this.operationMessage = '';
      } else {
        this.operationMessage = "Error during deposit";
      }
    })
  }

  loadUpdatedCard() {
    const cardContent = `${this.CVV}:${this.balance}:${this.accountNumber}`;
    this.saveToFile('card.txt', cardContent);
  }
}
