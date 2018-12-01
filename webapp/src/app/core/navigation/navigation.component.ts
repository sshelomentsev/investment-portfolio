import { Component, OnInit } from '@angular/core';

import { MatSnackBar } from '@angular/material';
import { AuthService } from '../../common/auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent implements OnInit {

  public topMenuItems: any[] = [{
    link: 'performance',
    name: 'Fund performance'
  },
  {
    link: 'transactions',
    name: 'Transactions'
  },
  {
    link: 'profile',
    name: 'Profile'
  }];

  constructor(private auth: AuthService, private router: Router) {
    this.topMenuItems = this.topMenuItems.map(item => {
      item.display = auth.isAuthorized;
      return item;
    });
  }

  ngOnInit() {

  }

  public getUserName() {
    return this.auth.getUserName();
  }

  public onSelect(link: string) {
    this.router.navigate(['/' + link]);
  }

  public logout(): void {
    this.auth.logout();
  }

  public isAuthorized(): boolean {
    return this.auth.isAuthorized();
  }
}
