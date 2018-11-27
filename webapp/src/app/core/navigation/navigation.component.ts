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

  private readonly topMenuLinks = ['dashboard', 'players', 'teams'];

  public topMenuItems: any[] = [];

  constructor(private auth: AuthService, private router: Router) {
    this.topMenuItems = this.topMenuLinks.map((link) => {
      return {
        link: link,
        name: link,
        display: auth.isAuthorized || link !== 'dashboard'
      }
    });
  }

  ngOnInit() {

  }

  public onSelect(link: string) {
    this.router.navigate(['/' + link]);
  }

  public logout(): void {
    this.auth.logout();
    this.router.navigate(['/players']);
  }
}
