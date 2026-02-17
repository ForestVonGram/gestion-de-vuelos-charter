import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit, OnDestroy {
  isNavbarScrolled = false;
  parallaxOffset = 0;

  ngOnInit(): void {
    // Initialize scroll state
    this.checkScroll();
  }

  ngOnDestroy(): void {
    // Cleanup
  }

  @HostListener('window:scroll')
  onWindowScroll(): void {
    this.checkScroll();
    this.updateParallax();
  }

  private checkScroll(): void {
    // Get the hero element height and trigger navbar background change when scrolling past it
    const heroElement = document.querySelector('.hero');
    if (heroElement) {
      const heroHeight = heroElement.clientHeight;
      this.isNavbarScrolled = window.scrollY > heroHeight - 100;
    } else {
      this.isNavbarScrolled = window.scrollY > 100;
    }
  }

  private updateParallax(): void {
    const statementElement = document.querySelector('.statement');
    if (statementElement) {
      const rect = statementElement.getBoundingClientRect();
      const elementTop = rect.top;
      const windowHeight = window.innerHeight;

      // Calculate parallax offset when element is in view
      if (elementTop < windowHeight && elementTop > -rect.height) {
        const scrollProgress = (windowHeight - elementTop) / (windowHeight + rect.height);
        this.parallaxOffset = scrollProgress * 350; // Max 150px offset
        this.applyParallaxTransform();
      }
    }
  }

  private applyParallaxTransform(): void {
    const cloudLarge = document.querySelector('.cloud-large') as HTMLElement;
    const cloudSmall = document.querySelector('.cloud-small') as HTMLElement;
    const aircraft = document.querySelector('.aircraft') as HTMLElement;

    if (cloudLarge) {
      cloudLarge.style.transform = `translateY(${this.parallaxOffset * 0.2}px) scale(1.05)`;
    }
    if (cloudSmall) {
      cloudSmall.style.transform = `translateY(${this.parallaxOffset * 0.4}px) scale(1.03)`;
    }
    if (aircraft) {
      aircraft.style.transform = `translateY(${this.parallaxOffset * 0.1}px) scale(1.02)`;
    }
  }
}
